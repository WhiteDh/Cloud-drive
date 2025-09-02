CREATE TABLE IF NOT EXISTS folder (
                        id BIGSERIAL PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        path VARCHAR(1024) NOT NULL,
                        parent_folder_id BIGINT,
                        owner_id BIGINT NOT NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT now(),
                        CONSTRAINT fk_folder_parent FOREIGN KEY (parent_folder_id) REFERENCES folder(id) ON DELETE SET NULL,
                        CONSTRAINT fk_folder_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
);
