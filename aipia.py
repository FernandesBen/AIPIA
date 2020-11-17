import pandas as pd
from dateutil import parser
from sklearn.preprocessing import MinMaxScaler
import numpy as np
import math
from keras.models import Sequential
from keras.layers import Dense, LSTM, Bidirectional
from keras.optimizers import Adam
import matplotlib.pyplot as plt


def next_30(averageFileLoc, datesArray, aiDataArray, scaler, model):
    #create dataframe for next 30 day predictions
    datesArray = datesArray[-len(aiDataArray):]
    aiDataArray = [i[0] for i in aiDataArray]
    inputData = pd.DataFrame({"dates":datesArray,"Predictions":aiDataArray})
    inputData = inputData.set_index("dates")
    pred_dates = pd.date_range(start=datesArray[-1], periods=30)
    pred_dates = [i.strftime("%Y-%m-%d") for i in pred_dates]
    pred_df =  pd.DataFrame(data={"dates":pred_dates,"Predictions":pred_dates})
    pred_df =  pred_df.set_index("dates")
    pred_df = inputData.append(pred_df)

    #predict the revenue or inventory for the next 30 days
    i=0
    while i<30:
        last_30_days=pred_df["Predictions"][-(60-i):-(30-i)]
        last_30_days = [[i] for i in last_30_days]
        last_30_days = np.array(last_30_days)
        last_30_days_scaled=scaler.transform(last_30_days)
        prediction_31 = []
        prediction_31.append(last_30_days_scaled)
        prediction_31 = np.array(prediction_31)
        prediction_31 = np.reshape(prediction_31, (prediction_31.shape[0], prediction_31.shape[1],1))
        price_31 = model.predict(prediction_31)
        price_31 = scaler.inverse_transform(price_31)
        pred_df.iloc[-(30-i)]["Predictions"]=price_31
        i+=1

    #calculate the sum of the inventory or revenue and save to a file
    nextMonth = round(pred_df.iloc[-30:].sum(),0)
    file = open(averageFileLoc,"w")
    file.truncate(0)
    file.write(str(nextMonth[0]))
    file.close()


def results(outputFileLoc, averageFileLoc, dateArray, predictionArray):
    #make sure file is empty
    file = open(outputFileLoc, "w")
    file.truncate(0)
    file.close()

    #save results of tested predictions to a file
    predictionArray = [i[0] for i in predictionArray]
    dateArray = dateArray[-len(predictionArray):]
    df = pd.DataFrame({"Dates":dateArray,"Predictions":predictionArray})
    df.to_csv(outputFileLoc)


def buildModel(x_training_data):
    # Create BiLSTM model
    model = Sequential()
    model.add(Bidirectional(LSTM(16, return_sequences=True), input_shape=(x_training_data.shape[1], x_training_data.shape[2])))
    model.add(Bidirectional(LSTM(16, return_sequences=False)))
    model.add(Dense(16))
    model.add(Dense(1))
    model.compile(optimizer='adam',loss='mse')
    return model

    #Compile the model
    opt = Adam(lr=1e-3,decay=1e-5)
    model.compile(optimizer = opt, loss='mean_squared_error')
    return model


def trainModel(model,x_train,y_train,batchNum,epochNum):
    #train model
    model.fit(x_train,y_train,batch_size=batchNum,epochs=epochNum)
    return model


def splitXY(df, sequenceSize): #x independent, y dependent/target
    #split independent variable to predict the dependent variable
    x = []
    y = []

    for i in range(sequenceSize, len(df)):
        x.append(df[i-sequenceSize:i,0])
        y.append(df[i,0])

    x, y = np.array(x), np.array(y)
    x = np.reshape(x, (x.shape[0],x.shape[1],1)) #number of samples, number of timesteps (30), number of features (daily revenue or inventory)
    return x, y

def splitTrainTest(sales,percentTrain,scaler,sequenceSize):
    #where to split the data
    training_data_len = math.ceil(len(sales)*percentTrain)

    #scale data
    scaled_data = scaler.fit_transform(sales)

    #split into training and testing datasets
    train_data = scaled_data[:training_data_len,:]
    test_data = scaled_data[training_data_len-sequenceSize:,:]

    #split into x, y for training and testing datasets
    x_train, y_train = splitXY(train_data, sequenceSize)
    x_test, y_test = splitXY(test_data, sequenceSize)
    return x_train, y_train, x_test, y_test, training_data_len


def dataFormatter(dateColumn,targetColumn, df):
    #format dates and data for the model
    dates = df[dateColumn].unique()
    data = []
    for date in dates:
        sameDate = df.loc[df[dateColumn]==date]
        sameDateSum = round(sameDate[targetColumn].sum(),2)
        data.append([sameDateSum])
    return dates, data


def dateFormatter(period, dateColumn, df):
    #format the date string to a certain format
    df[dateColumn] = [parser.parse(date).strftime("%Y-%m-%d") for date in df[dateColumn]]
    return df


def dataProcessing(inputFileLoc, dataFileLoc, scaler):
    #read input data
    inputData = pd.read_csv(dataFileLoc, encoding="unicode_escape")

    #read variables data
    input = open(inputFileLoc, "r")
    vars = input.readline().strip("\n").split(",")
    input.close()

    #format data depending on if it is revenue or inventory
    inputDf = dateFormatter(vars[0], vars[1], inputData)
    if(len(vars)==4):
        inputDf["Sale"] = round(inputDf[vars[2]]*inputDf[vars[3]])
        targetColumn = "Sale"
    else:
        inputDf = inputDf.loc[inputDf[vars[3]] == vars[4]]
        targetColumn = vars[2]
    dates, aiData =  dataFormatter(vars[1], targetColumn, inputDf)

    #format data to be fed into the AI model
    x_train, y_train, x_test, y_test, training_data_len = splitTrainTest(aiData, .8, scaler, 30)
    return x_train, y_train, x_test, y_test, training_data_len, dates, aiData


def main(inputFileLoc, dataFileLoc, outputFileLoc, averageFileLoc):
    #importing data
    scaler = MinMaxScaler(feature_range=(0,1))
    x_train_array, y_train_array, x_test_array, y_test_array, training_data_length, datesArray, aiDataArray = dataProcessing(inputFileLoc, dataFileLoc, scaler)
    
    #modeling
    model = buildModel(x_train_array)
    model = trainModel(model, x_train_array,y_train_array,8,2)
    
    #predicting
    predictions = model.predict(x_test_array)
    predictions = scaler.inverse_transform(predictions)

    #writing predictions to file
    results(outputFileLoc, averageFileLoc, datesArray, predictions)
    next_30(averageFileLoc, datesArray, aiDataArray, scaler, model)

inputFileLoc = "/content/drive/My Drive/CSC 548/inventory.csv"
dataFileLoc = "/content/drive/My Drive/CSC 548/ukecommerce.csv"
outputFileLoc = "/content/drive/My Drive/CSC 548/predictions.csv"
averageFileLoc = "/content/drive/My Drive/CSC 548/average.txt"
main(inputFileLoc, dataFileLoc, outputFileLoc, averageFileLoc)