INSERT  INTO roles(name) VALUES('ROLE_USER'),('ROLE_ADMIN'),('ROLE_MONITOR');
INSERT  INTO positions(name) VALUES('PRESIDENT'),('V_PRESIDENT'),('GENERAL_SECRETARY'),('V_GENERAL_SECRETARY'),('PUBLICITY_SECRETARY'),('V_PUBLICITY_SECRETARY'),('PROJECT_COORDINATOR'),('V_PROJECT_COORDINATOR'),('FIRST_1_YEAR_REPRESENTATIVE'),('FIRST_2_YEAR_REPRESENTATIVE'),('SECOND_YEAR_REPRESENTATIVE'),('THIRD_YEAR_REPRESENTATIVE'),('TREASURE');
INSERT INTO members(first_name, last_name, reg_number, password,email, is_disabled,year)VALUES ('John', 'Doe','bsc-49-16', '$2y$12$IAJfCPSb5D.jBDdin/CXd.rM6MP672kHR94qSCdN2GGVRap47FpP2', 'bsc-49-16@cc.ac.mw', false, 4);
INSERT INTO user_roles(user_id, role_id)VALUES (1, 2);

