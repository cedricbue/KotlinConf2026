delete from STOCK_EVENTS;
delete from STOCKS;

insert into STOCKS(id, symbol, price) values  (default, 'AAPL',124.12);
insert into STOCKS(id, symbol, price) values (default, 'MSFT', 246.15);
insert into STOCKS(id, symbol, price) values (default, 'AMZN',3213.12);
insert into STOCKS(id, symbol, price) values (default, 'GOOG',2314.20);

insert into STOCK_EVENTS(id, type, symbol, price) values  (default,  'MODIFIED', 'AAPL',124.12);
insert into STOCK_EVENTS(id, type, symbol, price) values (default, 'MODIFIED','MSFT', 246.15);
insert into STOCK_EVENTS(id, type, symbol, price) values (default, 'MODIFIED','AMZN',3213.12);
insert into STOCK_EVENTS(id, type, symbol, price) values (default, 'MODIFIED','GOOG',2314.20);

select * from STOCKS;
select * from STOCK_EVENTS  ;
