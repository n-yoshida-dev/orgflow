INSERT INTO
  users (
    display_name,
    login_id,
    hashed_password,
    mail_address
  )
VALUES
  (
    'テスト 太郎',
    'test_taro',
    '{bcrypt}$2a$10$Jr7WLoKeRHwpnBy4IeGxquiL7TM4J9yFYEsZLNLbOob8qtyRpSv8i',
    'taro-test@example.com'
  ),
  (
    '川崎　一郎',
    'ichiro_kawasaki',
    '{bcrypt}$2a$10$4faCenFNy0igfGj6pmquFu1R5GsxKemkWOxsan3eASSlK60IeuUAu',
    'ichiro-kawasaki@example.com'
  );
