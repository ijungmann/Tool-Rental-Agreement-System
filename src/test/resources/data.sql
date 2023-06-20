INSERT INTO tool_type (name, daily_charge, holiday_charge, weekday_charge, weekend_charge)
VALUES ('Ladder', 1.99, 0, 1, 1);
INSERT INTO tool_type (name, daily_charge, holiday_charge, weekday_charge, weekend_charge)
VALUES ('Chainsaw', 1.49, 1, 1, 0);
INSERT INTO tool_type (name, daily_charge, holiday_charge, weekday_charge, weekend_charge)
VALUES ('Jackhammer', 2.99, 0, 1, 0);

INSERT INTO tool (`type`, brand, code) VALUES ('Ladder', 'Werner', 'LADW');
INSERT INTO tool (`type`, brand, code) VALUES ('Chainsaw', 'Stihl', 'CHNS');
INSERT INTO tool (`type`, brand, code) VALUES ('Jackhammer', 'Ridgid', 'JAKR');
INSERT INTO tool (`type`, brand, code) VALUES ('Jackhammer', 'DeWalt', 'JAKD');