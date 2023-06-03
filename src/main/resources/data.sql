INSERT INTO area (NAME, active, here, displayOrder) VALUES ('צפון', 1, 0, 0);

INSERT INTO area (NAME, active, here, displayOrder) VALUES (N'מרכז', 1, 0, 0);

INSERT INTO area (NAME, active, here, displayOrder) VALUES (N'דרום', 1, 0, 0);

INSERT INTO area (NAME, active, here, displayOrder) VALUES (N'מוסך', 1, 1, 0);

INSERT INTO call (custID, siteID, contactID, carTypeID, callTypeID, Notes, startdate) VALUES (1, 1, 1, 1, 1, 'sample notes','2018-10-31');

INSERT INTO call (custID, siteID, contactID, carTypeID, callTypeID, Notes, startdate, date2) VALUES (1, 1, 1, 1, 2, '','2018-10-30','2018-11-05');

INSERT INTO callType (NAME, active) VALUES (N'טיפול', 1);

INSERT INTO callType (NAME, active) VALUES (N'תקלה', 1);

INSERT INTO callType (NAME, active) VALUES (N'טיפול+תקלה', 1);

INSERT INTO carType (NAME, active) VALUES (N'קלאב קאר', 1);

INSERT INTO carType (NAME, active) VALUES (N'מכסחת', 1);

INSERT INTO cust (NAME, custtype, active) VALUES (N'לקוח לדוגמה', 1, 1);

INSERT INTO custType (NAME, active) VALUES (N'עסקי', 1);

INSERT INTO custType (NAME, active) VALUES (N'פרטי', 1);

INSERT INTO driver (NAME, active) VALUES ('נהג1', 1);

INSERT INTO driver (NAME, active) VALUES (N'נהג2', 1);

INSERT INTO driver (NAME, active) VALUES (N'יוסי', 1);

INSERT INTO site (NAME, areaID, custID, active, contact, address) VALUES (N'אתר לדוגמה', 1, 1, 1, N'חנה', N'מעלות');
