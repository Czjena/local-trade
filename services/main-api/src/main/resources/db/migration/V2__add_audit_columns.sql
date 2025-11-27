ALTER TABLE users_entity ADD COLUMN created_by VARCHAR(255);
ALTER TABLE users_entity ADD COLUMN last_modified_by VARCHAR(255);

-- Opcjonalnie, żeby nie było nulli w starych rekordach
UPDATE users_entity SET created_by = 'system', last_modified_by = 'system' WHERE created_by IS NULL;