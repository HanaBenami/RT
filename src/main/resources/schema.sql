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
