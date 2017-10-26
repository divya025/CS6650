# To use the schema / database
USE SkiResort;

# To make sure there is no other table with same name
DROP TABLE IF EXISTS Record;

# To Create table Record with the following attributes
CREATE TABLE Record (
  RecordID INT AUTO_INCREMENT,
  SkierID INT,
  LiftID INT,
  DayNum INT,
  CONSTRAINT pk_Record_RecordID PRIMARY KEY (RecordID)
);
#  To empty the table contents
TRUNCATE TABLE Record;