CREATE TABLE chat (
  chat_id bigint(20) NOT NULL AUTO_INCREMENT,
  topic varchar(100),
  password varchar(255),
  PRIMARY KEY (chat_id)
);

CREATE TABLE user_accounts (
  user_id bigint(20) NOT NULL AUTO_INCREMENT,
  username varchar(100) NOT NULL,
  password varchar(100) NOT NULL,
  role varchar(100) DEFAULT NULL,
  name varchar(50) NOT NULL,
  pfp varchar(200) DEFAULT 'default-images/default-profile.png',
  PRIMARY KEY (user_id),
  UNIQUE (username)
);

CREATE TABLE message (
  message_id bigint(20) NOT NULL AUTO_INCREMENT,
  user_id bigint(20),
  chat_id bigint(20),
  date timestamp,
  is_new BOOLEAN,
  content varchar(255),
  PRIMARY KEY (message_id),
  FOREIGN KEY (user_id) REFERENCES user_accounts (user_id),
  FOREIGN KEY (chat_id) REFERENCES chat (chat_id)
);

CREATE TABLE notification (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  head varchar(50) DEFAULT NULL,
  body varchar(255) DEFAULT NULL,
  is_new tinyint(1),
  date timestamp NULL DEFAULT NULL,
  user_id bigint(20) DEFAULT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (user_id) REFERENCES user_accounts (user_id)
);

CREATE TABLE user_chat (
  chat_id bigint(20) NOT NULL,
  user_id bigint(20) NOT NULL,
  PRIMARY KEY (user_id, chat_id),
  FOREIGN KEY (user_id) REFERENCES user_accounts (user_id),
  FOREIGN KEY (chat_id) REFERENCES chat (chat_id)
);


