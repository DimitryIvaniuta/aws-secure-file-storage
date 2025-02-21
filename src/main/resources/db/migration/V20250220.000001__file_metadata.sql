CREATE TABLE file_metadata
(
    id          bigint       not null primary key,
    file_name   VARCHAR(255) NOT NULL,
    bucket_name VARCHAR(255) NOT NULL,
    file_size   BIGINT       NOT NULL,
    s3_key      TEXT         NOT NULL,
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    uploaded_by TEXT
);
