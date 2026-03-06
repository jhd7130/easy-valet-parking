ALTER TABLE parking_records ADD COLUMN guest_token VARCHAR(64) UNIQUE;
ALTER TABLE parking_records ADD COLUMN guest_token_expires_at TIMESTAMP;
CREATE INDEX idx_parking_guest_token ON parking_records(guest_token);
