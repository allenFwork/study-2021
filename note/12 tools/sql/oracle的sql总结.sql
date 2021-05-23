-- to_date的时分秒格式
select to_date('20210322142556', 'yyyyMMddhh24miss') from dual
-- to_char的时分秒格式
select to_char(sysdate, 'yyyyMMddhh24miss') from dual;
select to_char(sysdate, 'yyyy-MM-dd hh:mm:ss') from dual;
select to_char(sysdate, 'yyyy-MM-dd hh24:mm:ss') from dual;
-- 计算日期时间差值
select floor(to_date('2021-03-22 02:44:17','yyyy-mm-dd hh24:mi:ss')-to_date('2021-03-01 00:07:00 ','yyyy-mm-dd hh24:mi:ss')) from dual;
select floor(sysdate - to_date('2021-03-21 00:07:00 ','yyyy-mm-dd hh24:mi:ss')) from dual;
