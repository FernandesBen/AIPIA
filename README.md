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
  
  
Ex. text file for revenue <br>
/input/file/location.csv dateColumnName revenue volumeColumnName priceColumnName day /output/file/location.csv

Ex. text file for inventory <br>
/input/file/location.csv dateColumnName inventory volumeColumnName nameOfInventoryItemColumn nameOfInventoryItem month /output/file/location.csv
