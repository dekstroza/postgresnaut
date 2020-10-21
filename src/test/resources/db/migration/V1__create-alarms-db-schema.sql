CREATE TABLE IF NOT EXISTS alarms(id serial PRIMARY KEY,name VARCHAR (50)  NOT NULL,severity VARCHAR(20) NOT NULL);
INSERT into alarms (id,name,severity) values(101,'desa', 'pesa');
INSERT into alarms (id,name,severity) values(102,'pesa', 'desa');
INSERT into alarms (id,name,severity) values(202,'Put alarm', 'MEDIUM');