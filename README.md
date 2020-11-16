# AIPIA
Artificially Intelligent Predictive Inventory Analytics - CSC448
Predict the future inventory needs for a company using LTSM.

Input Interface
- importData
  - input file location
- dailyRevenue
  - string date format
    - name of column with date
  - revenue or inventory
    - name of volume column
    - if revenue
      - name of price column
    - if inventory
      - name of inventory item
      - name of inventory item column
  - day/month/year
- plotResults
  -output file location
  
Output Interface
- csv (date, predictions)
  - predictions for the next 3 day/month/year
- ERROR
  
  
Ex. csv file for revenue <br>
dateColumnName, volumeColumnName, priceColumnName, day

Ex. csv file for inventory <br>
dateColumnName, volumeColumnName, nameInventoryColumn, nameInventoryItem, month
