CREATE TABLE IF NOT EXISTS file (
                      id BIGSERIAL PRIMARY KEY,
                      name VARCHAR(255) NOT NULL,
                      path VARCHAR(1024) NOT NULL,
                      size BIGINT NOT NULL,
                      folder_id BIGINT,
                      owner_id BIGINT NOT NULL,
                      created_at TIMESTAMP NOT NULL DEFAULT now(),
                      CONSTRAINT fk_file_folder FOREIGN KEY (folder_id) REFERENCES folder(id) ON DELETE SET NULL,
                      CONSTRAINT fk_file_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
);
