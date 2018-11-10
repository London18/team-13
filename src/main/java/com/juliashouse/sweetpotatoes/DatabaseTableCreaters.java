package com.juliashouse.sweetpotatoes;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class DatabaseTableCreaters {
    @Autowired
    private JdbcTemplate template;

    public void createVisitUpdates() {
        String sql = "DROP TABLE VISITUPDATE CASCADE CONSTRAINTS;\n" +
                "CREATE TABLE VISITUPDATE(\n" +
                "visitID integer NOT NULL UNIQUE,\n" +
                "scID integer NOT NULL UNIQUE,\n" +
                "actions varchar(15)NOT NULL,\n" +
                "visitTime date NOT NULL,\n" +
                "comments varchar(255),\n" +
                "PRIMARY KEY(visitID),\n" +
                "FOREIGN KEY(scID)REFERENCES SCHEDULECARER(scID),\n" +
                "CONSTRAINT constraint1 CHECK(actions='arrived'OR actions='left'OR actions='home')";
        template.execute(sql);
    }

    public void createScheduleCarer() {
        String sql = "DROP TABLE SCHEDULECARER;\n" +
                "CREATE TABLE SCHEDULECARER(\n" +
                "scID integer NOT NULL UNIQUE,\n" +
                "SID integer NOT NULL UNIQUE,\n" +
                "payrollID integer NOT NULL UNIQUE,\n" +
                "PRIMARY KEY(scID),\n" +
                "FOREIGN KEY(SID)REFERENCES SCHEDULEEVENT(SID),\n" +
                "FOREIGN KEY(payrollID)REFERENCES CARERS(payrollID)\n" +
                ");";
        template.execute(sql);
    }

    public void createScheduleEvent() {
        String sql = "DROP TABLE SCHEDULEEVENT;\n" +
                "CREATE TABLE SCHEDULEEVENT(\n" +
                "SID integer NOT NULL UNIQUE,\n" +
                "start date NOT NULL,\n" +
                "end date NOT NULL,\n" +
                "fID integer NOT NULL UNIQUE,\n" +
                "PRIMARY KEY(SID),\n" +
                "FOREIGN KEY(fID)REFERENCES FAMILY(fID)\n" +
                ");";
        template.execute(sql);
    }

    public void createFamily() {
        String sql = "DROP TABLE FAMILY;\n" +
                "CREATE TABLE FAMILY(\n" +
                "fID integer NOT NULL UNIQUE,\n" +
                "name varchar(50)NOT NULL,\n" +
                "address varchar(50)NOT NULL,\n" +
                "PRIMARY KEY(fID)\n" +
                ");";
        template.execute(sql);
    }

    public void createCarers() {
        String sql = "DROP TABLE CARERS;\n" +
                "CREATE TABLE CARERS (\n" +
                "payrollID integer NOT NULL UNIQUE,\n" +
                "firstName varchar(50) NOT NULL,\n" +
                "lastName varchar(50) NOT NULL,\n" +
                "phone varchar(15) NOT NULL,\n" +
                "address varchar(50) NOT NULL,\n" +
                "userID integer NOT NULL UNIQUE,\n" +
                "PRIMARY KEY (PayrollID),\n" +
                "FOREIGN KEY (userID) REFERENCES USERS(userID)\n" +
                ");";
        template.execute(sql);
    }

    public void createUsers() {
        String sql = "DROP TABLE USERS;\n" +
                "CREATE TABLE USERS(\n" +
                "userID integer NOT NULL UNIQUE,\n" +
                "email varchar(100)NOT NULL,\n" +
                "password varchar(50)NOT NULL,\n" +
                "salt blob NOT NULL,\n" +
                "iterations integer NOT NULL,\n" +
                "keysize integer NOT NULL,\n" +
                "PRIMARY KEY(userID)\n" +
                ");";
        template.execute(sql);
    }
}
