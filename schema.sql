DROP TABLE CARERS;
CREATE TABLE CARERS (
    payrollID integer NOT NULL UNIQUE,
    firstName varchar(50) NOT NULL,
    lastName varchar(50) NOT NULL,
    phone varchar(15) NOT NULL,
    address varchar(50) NOT NULL,
    userID integer NOT NULL UNIQUE,
    PRIMARY KEY (PayrollID),
    FOREIGN KEY (userID) REFERENCES USERS(userID)
);

DROP TABLE USERS;
CREATE TABLE USERS (
    userID integer NOT NULL UNIQUE,
    email varchar(100) NOT NULL,
    password varchar(50) NOT NULL,
    salt blob NOT NULL,
    iterations integer NOT NULL,
    keysize integer NOT NULL,
    PRIMARY KEY (userID)
);

DROP TABLE FAMILY;
CREATE TABLE FAMILY (
    fID integer NOT NULL UNIQUE,
    name varchar(50) NOT NULL,
    address varchar(50) NOT NULL,
    PRIMARY KEY (fID)
);

DROP TABLE SCHEDULEEVENT;
CREATE TABLE SCHEDULEEVENT (
    SID integer NOT NULL UNIQUE,
    start date NOT NULL,
    end date NOT NULL,
    fID integer NOT NULL UNIQUE,
    PRIMARY KEY (SID),
    FOREIGN KEY (fID) REFERENCES FAMILY(fID)
);

DROP TABLE SCHEDULECARER;
CREATE TABLE SCHEDULECARER(
    scID integer NOT NULL UNIQUE,
    SID integer NOT NULL UNIQUE,
    payrollID integer NOT NULL UNIQUE,
    PRIMARY KEY (scID),
    FOREIGN KEY (SID) REFERENCES SCHEDULEEVENT(SID),
    FOREIGN KEY (payrollID) REFERENCES CARERS(payrollID)
);

DROP TABLE VISITUPDATE CASCADE CONSTRAINTS;
CREATE TABLE VISITUPDATE (
    visitID integer NOT NULL UNIQUE,
    scID integer NOT NULL UNIQUE,
    actions varchar(15) NOT NULL,
    visitTime date NOT NULL,
    comments varchar(255),
    PRIMARY KEY (visitID),
    FOREIGN KEY (scID) REFERENCES SCHEDULECARER(scID),
    CONSTRAINT constraint1 CHECK (actions = 'arrived' OR actions = 'left' OR actions = 'home')
);