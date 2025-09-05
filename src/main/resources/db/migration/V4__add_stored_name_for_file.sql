ALTER TABLE file
    ADD COLUMN stored_name VARCHAR(255) DEFAULT 'unknown' NOT NULL;
;