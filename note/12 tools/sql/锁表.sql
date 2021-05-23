https://www.cnblogs.com/lcword/p/12483227.html

-- ��ѯ������ͼ(oracle)  (���Ի���ֻ��picc��Ȩ��)
select * from v$locked_object;

-- ��ѯsession��Ϣ��ͼ
select * from v$session where username = 'PICC_EBSS';

select * from dba_objects;

-- ��ѯ�������Ϣ
select b.owner,b.object_name,a.session_id,a.locked_mode 
    from v$locked_object a,dba_objects b
    where b.object_id = a.object_id and a.session_id = '1049';
    
    
select b.username,b.sid,b.serial#,logon_time,b.STATUS,b.MACHINE
    from v$locked_object a,v$session b
    where a.session_id = b.sid order by b.logon_time;
    
select * from (select b.owner,b.object_name,a.session_id,a.locked_mode 
    from v$locked_object a,dba_objects b
    where b.object_id = a.object_id) m,(select b.username,b.sid,b.serial#,logon_time,b.STATUS,b.MACHINE
    from v$locked_object a,v$session b
    where a.session_id = b.sid) n where m.session_id = n.sid and m.session_id = '323';


select b.owner,b.object_name,a.session_id,a.locked_mode ,a.OS_USER_NAME,c.SERIAL#
    from v$locked_object a,dba_objects b, v$session c
    where b.object_id = a.object_id and a.SESSION_ID = c.SID

-- ��ѯ����ִ�е�sql
SELECT b.sid oracleID,
    b.username ��¼Oracle�û���,
    b.serial#,
    spid ����ϵͳID,
    paddr,
    sql_text ����ִ�е�SQL,
    b.machine �������
FROM v$process a, v$session b, v$sqlarea c
WHERE a.addr = b.paddr
  AND b.sql_hash_value = c.hash_value
  and b.SERIAL# = '33886';

-- ɱ��session_id���������״̬
alter system kill session 'v$session.sid,v$session.serial#';
alter system kill session '1049,17809';


SELECT ��s.username,
       decode(l.type, 'TM', 'TABLE LOCK', 'TX', 'ROW LOCK', NULL) LOCK_LEVEL,
       ����o.object_name,
       ����s.sid��,
       l.block��FROM v$session s,
       v$lock l,
       dba_objects o����WHERE s.username = 'OCBC' and l.sid = s.sid����AND l.id1 = o.object_id(+) ����AND s.username is NOT NULL order by s.username;

select distinct b.sid, a.piece, a.sql_text
  from v$sqltext_with_newlines a,
       (select decode(s.sql_address, '00', s.prev_sql_addr, sql_address) sql_address,
               decode(s.sql_hash_value,
                      0,
                      s.prev_hash_value,
                      s.sql_hash_value) sql_hash_value,
               s.sid
          from v$session s, v$lock l
         WHERE l.sid = s.sid
           and l.block = 1) b
 where rawtohex(a.address) = b.sql_address
   and a.hash_value = b.sql_hash_value
   --and b.sid = '323'
 order by b.sid, a.piece ASC;
 
select a.sid, b.spid from (select s.sid, s.paddr from v$session s,v$lock l WHERE l.sid = s.sid and l.block=1) a, v$process b where a.paddr=b.addr; 


�鿴��ռ�


SELECT TABLESPACE_NAME "��ռ�",
       To_char(Round(BYTES / 1024, 2), '99990.00')
       || ''           "ʵ��",
       To_char(Round(FREE / 1024, 2), '99990.00')
       || 'G'          "����",
       To_char(Round(( BYTES - FREE ) / 1024, 2), '99990.00')
       || 'G'          "ʹ��",
       To_char(Round(10000 * USED / BYTES) / 100, '99990.00')
       || '%'          "����"
FROM   (SELECT A.TABLESPACE_NAME                             TABLESPACE_NAME,
               Floor(A.BYTES / ( 1024 * 1024 ))              BYTES,
               Floor(B.FREE / ( 1024 * 1024 ))               FREE,
               Floor(( A.BYTES - B.FREE ) / ( 1024 * 1024 )) USED
        FROM   (SELECT TABLESPACE_NAME TABLESPACE_NAME,
                       Sum(BYTES)      BYTES
                FROM   DBA_DATA_FILES
                GROUP  BY TABLESPACE_NAME) A,
               (SELECT TABLESPACE_NAME TABLESPACE_NAME,
                       Sum(BYTES)      FREE
                FROM   DBA_FREE_SPACE
                GROUP  BY TABLESPACE_NAME) B
        WHERE  A.TABLESPACE_NAME = B.TABLESPACE_NAME)
--WHERE TABLESPACE_NAME LIKE 'CDR%' --��һ������ָ����ռ�����
ORDER  BY Floor(10000 * USED / BYTES) DESC;