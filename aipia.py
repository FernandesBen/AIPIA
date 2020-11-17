import pandas as pd
from dateutil import parser
from sklearn.preprocessing import MinMaxScaler
import numpy as np
import math
from keras.models import Sequential
from keras.layers import Dense, LSTM
import matplotlib.pyplot as plt

def results(outputFileLoc, averageFileLoc, dateArray, predictionArray):
    file = open(outputFileLoc, "w")
    file.truncate(0)
    file.close()
    predictionArray = [i[0] for i in predictionArray]
    dateArray = dateArray[-len(predictionArray):]
    df = pd.DataFrame({"Dates":dateArray,"Predictions":predictionArray})
    df.to_csv(outputFileLoc)

def buildModel(x_training_data):
    #build model
    model=Sequential()
    model.add(LSTM(50, return_sequences=True, input_shape=(x_training_data.shape[1], 1)))
    model.add(LSTM(50, return_sequences=False))
    model.add(Dense(25))
    model.add(Dense(1))

    #Compile the model
    model.compile(optimizer = 'adam', loss='mean_squared_error')
    return model

def trainModel(model,x_train,y_train,batchNum,epochNum):
    model.fit(x_train,y_train,batch_size=batchNum,epochs=epochNum)
    return model

def splitXY(df, sequenceSize): #x independent, y dependent/target
    x = []
    y = []

    for i in range(sequenceSize, len(df)):
        x.append(df[i-sequenceSize:i,0])
        y.append(df[i,0])

    x, y = np.array(x), np.array(y)
    x = np.reshape(x, (x.shape[0],x.shape[1],1)) #number of samples (301), number of timesteps (60), number of features (daily_sale)
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
    dates = df[dateColumn].unique()
    data = []
    for date in dates:
        sameDate = df.loc[df[dateColumn]==date]
        sameDateSum = round(sameDate[targetColumn].sum(),2)
        data.append([sameDateSum])
    return dates, data

def dateFormatter(period, dateColumn, df):
    df[dateColumn] = [parser.parse(date).strftime("%Y-%m-%d") for date in df[dateColumn]]
    return df

def dataProcessing(inputFileLoc, dataFileLoc, scaler):
    inputData = pd.read_csv(dataFileLoc, encoding="unicode_escape")

    input = open(inputFileLoc, "r")
    vars = input.readline().strip("\n").split(",")
    input.close()
    inputDf = dateFormatter(vars[0], vars[1], inputData)
    if(len(vars)==4):
        inputDf["Sale"] = round(inputDf[vars[2]]*inputDf[vars[3]])
        targetColumn = "Sale"
    else:
        inputDf = inputDf.loc[inputDf[vars[3]] == vars[4]]
        targetColumn = vars[2]
    dates, aiData =  dataFormatter(vars[1], targetColumn, inputDf)

    x_train, y_train, x_test, y_test, training_data_len = splitTrainTest(aiData, .8, scaler, 30)
    return x_train, y_train, x_test, y_test, training_data_len, dates, aiData, inputData

def main(inputFileLoc, dataFileLoc, outputFileLoc, averageFileLoc):
    #importing data
    scaler = MinMaxScaler(feature_range=(0,1))
    x_train_array, y_train_array, x_test_array, y_test_array, training_data_length, datesArray, aiDataArray, inputData = dataProcessing(inputFileLoc, dataFileLoc, scaler)

    #modeling
    model = buildModel(x_train_array)
    model = trainModel(model, x_train_array,y_train_array,1,1)
    
    #predicting
    predictions = model.predict(x_test_array)
    predictions = scaler.inverse_transform(predictions)
    results(outputFileLoc, averageFileLoc, datesArray, predictions)

inputFileLoc = "/content/drive/My Drive/CSC 548/inventory.csv"
dataFileLoc = "/content/drive/My Drive/CSC 548/ukecommerce.csv"
outputFileLoc = "/content/drive/My Drive/CSC 548/predictions.csv"
averageFileLoc = "/content/drive/My Drive/CSC 548/average.txt"
main(inputFileLoc, dataFileLoc, outputFileLoc, averageFileLoc)