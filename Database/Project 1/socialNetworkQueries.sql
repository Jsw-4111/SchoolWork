#1
SELECT *
FROM comments
WHERE Text LIKE "%OMG%"
ORDER BY comments.Poster ASC, comments.PostDate DESC
;

#2
SELECT DISTINCT *
FROM users
WHERE Gender = 'M' AND ID in
	(
		SELECT ID1
        FROM Friends
        WHERE ID2 in
        (
			SELECT ID
            FROM users
            WHERE Gender = 'F'
        )
    );
    
#3
SELECT *
FROM Friends
WHERE STARTDATE > '2018-12-31' AND ID1 in
	(
		SELECT ID
		FROM users
		WHERE Gender = 'M' AND ID2 in
		(
			SELECT ID
			FROM users
			WHERE Gender = 'F'
		)
	)
UNION
SELECT *
FROM Friends
WHERE STARTDATE > '2018-12-31' AND ID1 in
	(
		SELECT ID
		FROM users
		WHERE Gender = 'F' AND ID2 in
		(
			SELECT ID
			FROM users
			WHERE Gender = 'M'
		)
	);
    
#4
SELECT users.*,
(
	SELECT COUNT(*)
    FROM Friends
    WHERE ID1 = users.ID AND ID2 in
    (
		SELECT ID 
        FROM users
        WHERE Gender = 'M'
	)
) as M, (
	SELECT COUNT(*)
	FROM Friends
	WHERE ID1 = users.ID AND ID2 in
	(
		SELECT ID 
        FROM users
        WHERE Gender = 'F'
	)
) as F
FROM users;

#5
SELECT DISTINCT users.*
FROM users
WHERE NOT EXISTS
(
	SELECT *
    FROM comments
    WHERE users.ID = RECIPIENT AND POSTER in
        (
			SELECT ID
            FROM users
            WHERE Gender = 'F'
        )
);

#6
SELECT users.*
FROM users
WHERE
(
	SELECT COUNT(*) as posts
    FROM comments
    WHERE users.ID = Poster
    HAVING posts > 1
);

#7
SELECT Name, Text, PostDate
FROM users, comments
WHERE ID = Poster AND
(
	SELECT COUNT(*)
    FROM comments
    WHERE users.ID = POSTER
    HAVING COUNT(*) > 1
);

#8 NOT DONE
SELECT DISTINCT post.*
FROM users post, users receive, comments
WHERE POSTER = post.ID 
	AND RECIPIENT = receive.ID 
	AND receive.Gender = 'F' 
	AND POSTER NOT IN (
		SELECT DISTINCT postc.ID
        FROM users postc, users postd
        WHERE postd.Gender = 'F' AND NOT EXISTS (
			SELECT *
            FROM COMMENTS
            WHERE POSTER = postc.ID AND RECIPIENT = postd.ID
        )
	)
;


#9
SELECT users.*, (
	SELECT COUNT(*)
    FROM comments
    WHERE users.ID = RECIPIENT
) as commented
FROM users
ORDER BY commented desc
LIMIT 1