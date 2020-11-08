DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS Friends;
DROP TABLE IF EXISTS users;
CREATE TABLE users(
	ID VARCHAR(5) Primary Key, Name varchar(20) NOT NULL, Gender char CHECK (GENDER IN('M', 'F'))
);
insert into users VALUES 
	(10001, 'John Wu', 'M'),
    (10002, 'Anderson Wu', 'M'),
    (10003, 'Feng Ming Liang', 'F'),
    (10004, 'Chi Chu Wu', 'M'),
    (10005, 'Link Wu', 'M'),
    (10006, 'Elly Wu', 'F'),
    (10007, 'Daisy Johnson', 'F');
CREATE TABLE Friends(
	ID1 VARCHAR(5) NOT NULL, ID2 VARCHAR(5), STARTDATE date, PRIMARY KEY (ID1, ID2), FOREIGN KEY (ID1) REFERENCES users(ID), FOREIGN KEY (ID2) REFERENCES users(ID)
);
insert into Friends VALUES
	(10001, 10002, '1998-11-26'),
    (10002, 10001, '1998-11-26'),
    (10001, 10003, '1998-11-26'),
    (10003, 10001, '1998-11-26'),
    (10001, 10004, '1998-11-26'),
    (10004, 10001, '1998-11-26'),
    (10001, 10005, '2013-10-17'),
    (10005, 10001, '2013-10-17'),
    (10001, 10006, '2013-10-17'),
    (10006, 10001, '2013-10-17'),
	(10002, 10003, '1994-11-30'),
    (10003, 10002, '1998-11-30'),
    (10002, 10004, '1994-11-30'),
    (10004, 10002, '1998-11-30'),
    (10002, 10005, '2013-10-17'),
    (10005, 10002, '2013-10-17'),
    (10007, 10001, '2019-10-16'),
    (10001, 10007, '2019-10-16');
CREATE TABLE comments(
	CommentID integer PRIMARY KEY, Poster VARCHAR(5), Recipient VARCHAR(5), Text VARCHAR(60) NOT NULL, PostDate date, FOREIGN KEY(Poster) REFERENCES users(ID), FOREIGN KEY (Recipient) REFERENCES users(ID)
);
insert into comments VALUES
	(1, 10001, 10002, "How's it going?", '2012-04-06'),
    (2, 10002, 10001, "It's pretty good, I got a 100 on that last test", '2012-04-06'),
    (3, 10001, 10002, "OMG", '2012-04-06'),
    (4, 10001, 10007, "Sounds like fun, what was it like?", '2019-10-17'),
    (5, 10005, 10003, "Hey, I'm 15 minutes out!", '2019-08-01'),
    (6, 10001, 10006, "Hey, don't forget to feed the dogs please!", '2019-10-22'),
    (7, 10003, 10002, "Hey, when did you say you were coming down again?", '2019-10-14'),
    (8, 10001, 10003, "Hey, how's it going?", '2019-02-06');