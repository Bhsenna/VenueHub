ALTER TABLE venues ADD COLUMN user_id INTEGER;

ALTER TABLE venues ADD CONSTRAINT fk_venues_users FOREIGN KEY(user_id) REFERENCES users(id)