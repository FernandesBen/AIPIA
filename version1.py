#import modules
import pandas as pd
from dateutil import parser
import numpy as np
import math
from sklearn.preprocessing import MinMaxScaler
from keras.models import Sequential
from keras.layers import Dense, LSTM
import matplotlib.pyplot as plt


def importData(csv_location):
  #import data in file
  return pd.read_csv(csv_location, encoding='unicode_escape')


def dailyRevenue(df, dateColumn):
  #format date column
  df[dateColumn] = [parser.parse(date).strftime('%Y-%m-%d') for date in df[dateColumn]]
  #create sale column
  df["Sale"] = [round(row[3]*row[5],2) for index, row in df.iterrows()]
  #array of dates
  dates = df[dateColumn].unique()
  #array of sales
  sales = []
  for date in dates:
    df_rows_with_date = df.loc[df[dateColumn]==date]
    df_rows_with_date_sum = round(df_rows_with_date["Sale"].sum(),2)
    sales.append([df_rows_with_date_sum])
  return df, dates, sales


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


def buildModel(x_training_data):
  #build model
  model=Sequential()
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


def plotResults(split, indexName, index_array, columnName, column_array, predictions_array):
  #create dataframe with all info
  column_array = [i[0] for i in column_array]
  data = {indexName:index_array,columnName:column_array}
  all_data = pd.DataFrame(data)
  all_data = all_data.set_index(indexName)

  #train and valid dataframes
  train_data = all_data[:split]
  test_data = all_data[split:]
  test_data['Predictions'] = predictions_array

  #create plot
  plt.figure(figsize=(16,8))
  plt.title('Results')
  plt.xlabel(indexName, fontsize=18)
  plt.ylabel(columnName, fontsize=18)
  plt.plot(train_data[columnName])
  plt.plot(test_data[[columnName,'Predictions']])
  plt.legend(['Train', 'Test', 'Predictions'], loc='upper left')
  plt.show()


def main(fileName, dateColumn):
  #data preprocessing
  ecommerce_df = importData(fileName)
  ecommerce_df, dates_array, sales_array = dailyRevenue(ecommerce_df, dateColumn)
  scaler = MinMaxScaler(feature_range=(0,1))
  x_train_array, y_train_array, x_test_array, y_test_array, training_data_length = splitTrainTest(sales_array, .8, scaler, 60)

  #modeling
  model = buildModel(x_train_array)
  model = trainModel(model, x_train_array,y_train_array,1,1)

  #predicting
  predictions = model.predict(x_test_array)
  rmse = np.sqrt(np.mean(predictions-y_test_array)**2)
  predictions = scaler.inverse_transform(predictions)
  plotResults(training_data_length, "Dates", dates_array, "Sales", sales_array, predictions)




fileLocation = '/content/drive/My Drive/CSC 548/ukecommerce.csv'
nameOfColumnWithDate = "InvoiceDate"

main(file)