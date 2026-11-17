
-- V2: Convert project auditing from user IDs to emails.

-- 1. Create temporary columns to hold email addresses.

ALTER TABLE org_projects
    ADD COLUMN created_by_email VARCHAR(255),
ADD COLUMN updated_by_email VARCHAR(255);


-- 2. Copy existing auditor emails using the old user IDs.

UPDATE org_projects p
SET created_by_email = u.email
    FROM users u
WHERE p.created_by = u.id;

UPDATE org_projects p
SET updated_by_email = u.email
    FROM users u
WHERE p.updated_by = u.id;


-- 3. Remove the old audit foreign-key constraints.

ALTER TABLE org_projects
DROP CONSTRAINT fk51xjwns7xkansa4xq6o71h5ot;

ALTER TABLE org_projects
DROP CONSTRAINT fkrnlskssy2khx5k98m72gpvn9c;


-- 4. Remove the old BIGINT columns.

ALTER TABLE org_projects
DROP COLUMN created_by,
DROP COLUMN updated_by;


-- 5. Rename the temporary columns.

ALTER TABLE org_projects
    RENAME COLUMN created_by_email TO created_by;

ALTER TABLE org_projects
    RENAME COLUMN updated_by_email TO updated_by;
