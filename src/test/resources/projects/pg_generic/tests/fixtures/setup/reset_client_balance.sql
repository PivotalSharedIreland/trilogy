UPDATE CLIENTS SET BALANCE=0 WHERE ID=66778899;
INSERT INTO TRANSACTIONS(ID_CLIENT, VALUE, TRANSACTION_TIME) VALUES (66778899, 500, CURRENT_TIMESTAMP - INTERVAL '10 minutes');
INSERT INTO TRANSACTIONS(ID_CLIENT, VALUE, TRANSACTION_TIME) VALUES (66778899, -100, CURRENT_TIMESTAMP - INTERVAL '9 minutes');
INSERT INTO TRANSACTIONS(ID_CLIENT, VALUE, TRANSACTION_TIME) VALUES (66778899, -100, CURRENT_TIMESTAMP - INTERVAL '8 minutes');
INSERT INTO TRANSACTIONS(ID_CLIENT, VALUE, TRANSACTION_TIME) VALUES (66778899, -100, CURRENT_TIMESTAMP - INTERVAL '7 minutes');
INSERT INTO TRANSACTIONS(ID_CLIENT, VALUE, TRANSACTION_TIME) VALUES (66778899, -200, CURRENT_TIMESTAMP - INTERVAL '6 minutes');
