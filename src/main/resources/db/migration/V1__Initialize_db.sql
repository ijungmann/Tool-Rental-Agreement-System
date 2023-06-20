CREATE TABLE IF NOT EXISTS tool_type (
    name VARCHAR(255) NOT NULL,
    daily_charge FLOAT NOT NULL,
    weekday_charge BOOLEAN NOT NULL,
    weekend_charge BOOLEAN NOT NULL,
    holiday_charge BOOLEAN NOT NULL,
    PRIMARY KEY (name)
);

CREATE TABLE IF NOT EXISTS tool (
    type VARCHAR(255) NOT NULL,
    brand VARCHAR(255) NOT NULL,
    code VARCHAR(255) NOT NULL,
    PRIMARY KEY (code),
    FOREIGN KEY (type) REFERENCES tool_type (name)
);

CREATE TABLE IF NOT EXISTS tool_rental_agreement (
    id INTEGER NOT NULL AUTO_INCREMENT,
    tool_code VARCHAR(255) NOT NULL,
    checkout_date DATE NOT NULL,
    due_date DATE NOT NULL,
    rental_days INTEGER NOT NULL,
    discount_percent FLOAT NOT NULL,
    charge_days INTEGER NOT NULL,
    pre_discount_charge FLOAT NOT NULL,
    discount_amount FLOAT NOT NULL,
    final_charge FLOAT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (tool_code) REFERENCES tool (code)
);