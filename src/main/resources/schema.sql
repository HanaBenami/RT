DROP VIEW IF EXISTS v_opencall;

DROP TABLE IF EXISTS area;
CREATE TABLE area (
	ID int IDENTITY(1,1) NOT NULL,
	NAME text NOT NULL DEFAULT '',
	active bit NOT NULL DEFAULT 1,
	here bit NOT NULL DEFAULT 0,
	displayOrder int NOT NULL DEFAULT 0);

DROP TABLE IF EXISTS callType;
CREATE TABLE callType (
	ID int IDENTITY(1,1) NOT NULL,
	NAME text NOT NULL DEFAULT '',
	active bit NOT NULL DEFAULT 1);

DROP TABLE IF EXISTS carType;
CREATE TABLE carType (
	ID int IDENTITY(1,1) NOT NULL,
	NAME text NOT NULL DEFAULT '',
	active bit NOT NULL DEFAULT 1);

DROP TABLE IF EXISTS custType;
CREATE TABLE custType (
	ID int IDENTITY(1,1) NOT NULL,
	NAME text NOT NULL DEFAULT '',
	active bit NOT NULL DEFAULT 1);

DROP TABLE IF EXISTS driver;
CREATE TABLE driver (
	ID int IDENTITY(1,1) NOT NULL,
	NAME text NOT NULL DEFAULT '',
	active bit NOT NULL DEFAULT 1);

DROP TABLE IF EXISTS cust;
CREATE TABLE cust(
	ID int IDENTITY(1,1) NOT NULL,
	NAME text NOT NULL DEFAULT '',
	custtype int NULL DEFAULT 0,
	active bit NOT NULL DEFAULT 1);

DROP TABLE IF EXISTS site;
CREATE TABLE site (
	ID int IDENTITY(1,1) NOT NULL,
	NAME text NOT NULL DEFAULT '',
	areaID int NULL DEFAULT 0,
	custID int NULL DEFAULT 0,
	active bit NOT NULL DEFAULT 1,
	contact text DEFAULT '',
	phone text NOT NULL DEFAULT '',
	notes text NOT NULL DEFAULT '',
	address text NOT NULL DEFAULT '');

DROP TABLE IF EXISTS call;
CREATE TABLE call (
	ID int IDENTITY(1,1) NOT NULL,
	custID int DEFAULT 0,
	siteID int DEFAULT 0,
	contactID int DEFAULT 0,
	carTypeID int DEFAULT 0,
	callTypeID int DEFAULT 0,
	Notes varchar(300) DEFAULT '',
	startdate date DEFAULT '1901-01-01',
	date1 date DEFAULT '1901-01-01',
	date2 date DEFAULT '1901-01-01',
	enddate date DEFAULT '1901-01-01',
	meeting bit DEFAULT 0,
	done bit DEFAULT 0,
	here bit DEFAULT 0,
	driverID int DEFAULT 0,
	workOrder int DEFAULT 0,
	descr text NOT NULL DEFAULT '');

CREATE INDEX ix_date2 on call(date2 desc);
CREATE INDEX ix_enddate on call(enddate desc);

CREATE VIEW v_opencall AS SELECT TOP 1000 call.ID, call.custID, call.siteID, call.contactID, call.carTypeID, call.callTypeID, call.Notes, call.startdate, call.date1, call.date2, call.enddate, call.meeting, call.done, call.here, call.driverID, call.workOrder, call.descr, site.areaID FROM call INNER JOIN site ON call.siteID = site.ID WHERE call.done = 0 ORDER BY call.date2;


-- 18/09/21

DROP VIEW v_opencall;

DROP TABLE IF EXISTS users;
CREATE TABLE users (ID int IDENTITY(1,1) NOT NULL, NAME text NOT NULL DEFAULT '', active bit NOT NULL DEFAULT 1);
INSERT INTO users (name) VALUES ('Hana');

DROP TABLE IF EXISTS contact;
CREATE TABLE contact ( ID int IDENTITY(1,1) NOT NULL,
	NAME text NOT NULL DEFAULT '',
	active bit NOT NULL DEFAULT 1,
	siteID int NULL DEFAULT 0,
	phone text NOT NULL DEFAULT '',
	notes text NOT NULL DEFAULT '');
INSERT INTO contact (name, phone, siteId) SELECT contact, phone, id FROM site;

ALTER TABLE call ADD deleted bit DEFAULT 0;
ALTER TABLE call ADD userId int DEFAULT 0;

CREATE VIEW v_opencall AS SELECT TOP 1000 call.ID, call.custID, call.siteID, call.contactID, call.carTypeID, call.callTypeID, call.Notes, call.startdate, call.date1, call.date2, call.enddate, call.meeting, call.done, call.here, call.driverID, call.workOrder, call.descr, site.areaID, call.deleted, call.userId FROM call INNER JOIN site ON call.siteID = site.ID WHERE call.done = 0 AND call.deleted = 0 ORDER BY call.date2;


-- 03/06/23 - VEHICLES

DROP TABLE IF EXISTS vehicle;
CREATE TABLE vehicle (id int IDENTITY(1, 1) NOT NULL, name text NOT NULL DEFAULT '', active bit NOT NULL DEFAULT 1, siteID int DEFAULT 0, typeID int DEFAULT 0, model text NOT NULL DEFAULT '', series text NOT NULL DEFAULT '', zama int DEFAULT 0, license int DEFAULT 0, engineHours int DEFAULT 0, lastUpdate date DEFAULT '1901-01-01');
insert into vehicle (name, typeID, siteId) select distinct cast(carType.name as varchar(200)), carType.id, siteId from call join carType on carType.id=call.carTypeID;

DROP VIEW v_opencall;
ALTER TABLE call ADD vehicleId int DEFAULT 0;
CREATE VIEW v_opencall AS SELECT TOP 1000 call.ID, call.custID, call.siteID, call.contactID, call.carTypeID, call.vehicleId, call.callTypeID, call.Notes, call.startdate, call.date1, call.date2, call.enddate, call.meeting, call.done, call.here, call.driverID, call.workOrder, call.descr, site.areaID, call.deleted, call.userId FROM call INNER JOIN site ON call.siteID = site.ID WHERE call.done = 0 AND call.deleted = 0 ORDER BY call.date2;
-- SET ANSI_NULLS, QUOTED_IDENTIFIER ON;
update call set call.vehicleId=(select vehicle.id from call join vehicle on vehicle.typeID=call.carTypeID and vehicle.siteID=call.siteID);

ALTER TABLE cust ADD hashkey int DEFAULT 0;

DROP TABLE IF EXISTS garageStatus;
CREATE TABLE garageStatus (ID int IDENTITY(1,1) NOT NULL, NAME text NOT NULL DEFAULT '', active bit NOT NULL DEFAULT 1,	pendingGarage bit NOT NULL DEFAULT 0, displayOrder int NOT NULL DEFAULT 0);
insert into garageStatus (name, pendingGarage, displayOrder) values ('Pending garage', 1, 1);
insert into garageStatus (name, pendingGarage, displayOrder) values ('Done', 0, 2);
ALTER TABLE call ADD garageStatusID int DEFAULT 0;


-- 18/06/23 - WAREHOUSE STATUS

DROP TABLE IF EXISTS warehouseStatus;
CREATE TABLE warehouseStatus (ID int IDENTITY(1,1) NOT NULL, NAME text NOT NULL DEFAULT '', active bit NOT NULL DEFAULT 1,	pendingWarehouse bit NOT NULL DEFAULT 0, displayOrder int NOT NULL DEFAULT 0);
insert into warehouseStatus (name, pendingWarehouse, displayOrder) values ('Pending warehouse', 1, 1);
insert into warehouseStatus (name, pendingWarehouse, displayOrder) values ('Done', 0, 2);
ALTER TABLE call ADD warehouseStatusID int DEFAULT 0;

update call set deleted=1 where id in (select id from call where custid=0 and deleted=0 and done=0 and here=0);
update call set done=1 where id in (select id from call where startdate<'2023-05-01' and deleted=0 and done=0 and here=0);


--19/06/23 - HASHAVSHEVET SYNC

DROP TABLE IF EXISTS city;
CREATE TABLE city (ID int IDENTITY(1,1) NOT NULL, name text NOT NULL DEFAULT '', active bit NOT NULL DEFAULT 1, areaID int NULL DEFAULT 0);
ALTER TABLE site ADD cityID int DEFAULT 0;

ALTER TABLE site ADD hashDocID int default 0;
ALTER TABLE cust ADD hashDocID int default 0;
ALTER TABLE vehicle ADD hashDocID int default 0;
ALTER TABLE contact ADD hashDocID int default 0;

DROP VIEW IF EXISTS v_hash_current_data;
DROP VIEW IF EXISTS v_hash_data_diff;
drop table if exists hash_data_already_merged;

-- create view v_hash_current_data as select DISTINCT 
-- 	StockMoves.id as DocumentID, 
-- 	Accounts.AccountKey as CustomerKey, 
-- 	Accounts.FullName as CustomerName, 
-- 	Accounts.Address as CustomerAddress, 
-- 	Accounts.City as CustomerCity, 
-- 	CONCAT(Accounts.Phone, ' ', Accounts.SPhone) as CustomerPhones, 
-- 	Stock.Address as SiteAddress, 
-- 	Stock.City as SiteCity, 
-- 	Stock.Contact as contact, 
-- 	StockMoves.BurdInstance as VehicleSeriesOrLicense, 
-- 	StockMoves.BurdInstItemKey as VehicleModel, 
-- 	Items.ItemName as VehicleType 
-- from rt_hash.dbo.StockMoves 
-- join rt_hash.dbo.Items on StockMoves.BurdInstItemKey=Items.ItemKey 
-- join rt_hash.dbo.Stock on Stock.ID=StockMoves.StockID 
-- join rt_hash.dbo.Accounts on Accounts.AccountKey=Stock.AccountKey 
-- where StockMoves.DocumentID=67;

create table hash_data_already_merged (
	ID int identity(1,1) not null, 
	DocumentID int not null, 
	DateAdded datetime default current_timestamp, 
	CustomerKey varchar(200) default '', 
	CustomerName varchar(200) default '', 
	CustomerAddress varchar(200) default '', 
	CustomerCity varchar(200) default '', 
	CustomerPhones varchar(200) default '', 
	SiteAddress varchar(200) default '', 
	SiteCity varchar(200) default '', 
	contact varchar(200) default '', 
	VehicleSeriesOrLicense varchar(200) default '', 
	VehicleModel varchar(200) default '', 
	VehicleType varchar(200) default ''
);

--create view v_hash_data_diff as select DocumentID, CustomerKey, CustomerName, CustomerAddress, CustomerCity, CustomerPhones, SiteAddress, SiteCity, contact, VehicleSeriesOrLicense, VehicleModel, VehicleType from v_hash_current_data except (select DocumentID, CustomerKey, CustomerName, CustomerAddress, CustomerCity, CustomerPhones, SiteAddress, SiteCity, contact, VehicleSeriesOrLicense, VehicleModel, VehicleType from hash_data_already_merged);


--10/02/24 - 02/03/24 - Invoices

ALTER TABLE call ADD invoiceNum int DEFAULT 0;
ALTER TABLE call ADD invoiceDocumentId int DEFAULT 0;

-- ALTER view [dbo].[v_hash_current_data] as 
-- 	select DISTINCT 
-- 		StockMoves.id as DocumentRowId, 
-- 		StockMoves.StockID as DocumentID, 
-- 		CASE 
-- 			WHEN Stock.DocumentID = 67 THEN 'WorkCard'
-- 			WHEN Stock.DocumentID = 79 THEN 'Invoice'
-- 			ELSE 'Unknown'
-- 		END AS DocumentType,
-- 		Accounts.AccountKey as CustomerKey, Accounts.FullName as CustomerName, 
-- 		Accounts.Address as CustomerAddress, 
-- 		Accounts.City as CustomerCity, 
-- 		CONCAT(Accounts.Phone, ' ', Accounts.SPhone) as CustomerPhones, 
-- 		Stock.Address as SiteAddress, 
-- 		Stock.City as SiteCity, 
-- 		Stock.Contact as contact, 
-- 		-- Work card
-- 		StockMoves.BurdInstance as VehicleSeriesOrLicense, 
-- 		StockMoves.BurdInstItemKey as VehicleModel, 
-- 		Items.ItemName as VehicleType, 
-- 		-- Invoice
-- 		StockMoves.quantity as Amount,
-- 		StockMoves.ItemName as ItemName,
-- 		Stock.DocNumber as InvoiceNum, 
-- 		Stock.ValueDate as InvoiceDate
-- 	from RAT2005.dbo.StockMoves 
-- 		join RAT2005.dbo.Stock on Stock.ID=StockMoves.StockID 
-- 		join RAT2005.dbo.Accounts on Accounts.AccountKey=Stock.AccountKey 
-- 		left outer join RAT2005.dbo.Items on StockMoves.BurdInstItemKey=Items.ItemKey 
-- 	where StockMoves.DocumentID in (79, 67)
-- GO

ALTER TABLE hash_data_already_merged ADD DocumentRowId int DEFAULT 0;
ALTER TABLE hash_data_already_merged ADD DocumentType varchar(200) DEFAULT null;
ALTER TABLE hash_data_already_merged ADD Amount decimal(5,3);
ALTER TABLE hash_data_already_merged ADD ItemName varchar(200) DEFAULT null;
ALTER TABLE hash_data_already_merged ADD InvoiceNum int DEFAULT 0;
ALTER TABLE hash_data_already_merged ADD InvoiceDate datetime DEFAULT null;

-- ALTER view [dbo].[v_hash_data_diff] as 
-- select DocumentRowId, DocumentID, DocumentType, CustomerKey, CustomerName, CustomerAddress, CustomerCity, CustomerPhones, SiteAddress, SiteCity, contact, 
-- VehicleSeriesOrLicense, VehicleModel, VehicleType, Amount, ItemName, InvoiceNum, InvoiceDate
-- from v_hash_current_data 
-- except 
-- (select DocumentRowId, DocumentID, DocumentType, CustomerKey, CustomerName, CustomerAddress, CustomerCity, CustomerPhones, SiteAddress, SiteCity, contact, 
-- VehicleSeriesOrLicense, VehicleModel, VehicleType, Amount, ItemName, InvoiceNum, InvoiceDate
-- from hash_data_already_merged)
-- GO

CREATE view [dbo].[v_hash_invoices_data] as 
SELECT [DocumentID]
      ,[DocumentRowId]
      ,[InvoiceNum]
      ,[InvoiceDate]
      ,[Amount]
      ,[ItemName]
  FROM [dbo].[hash_data_already_merged] as a 
  where a.DocumentType = 'Invoice' and a.id in (select max(b.id) from hash_data_already_merged as b where a.DocumentRowId = b.DocumentRowId) 
GO

ALTER TABLE hash_data_already_merged ALTER COLUMN Amount decimal(8,3);