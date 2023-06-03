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
