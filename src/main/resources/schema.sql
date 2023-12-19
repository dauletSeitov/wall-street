CREATE TABLE Users (
  user_id INTEGER PRIMARY KEY UNIQUE,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP,

  user_name VARCHAR(255) NOT NULL,
  balance INTEGER NOT NULL
);

CREATE TABLE Resources (
  id SERIAL PRIMARY KEY,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP,

  name VARCHAR(255) UNIQUE NOT NULL,
  unit VARCHAR(50) NOT NULL
);

CREATE TABLE Accounts (
  id SERIAL PRIMARY KEY,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP,

  user_id INTEGER NOT NULL,
  resource_id INTEGER NOT NULL,
  amount INTEGER NOT NULL,
  FOREIGN KEY(resource_id) REFERENCES Resources(id),
  UNIQUE(user_id, resource_id)
);

CREATE TABLE Purchases (
  id SERIAL PRIMARY KEY,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP,

  rate_id INTEGER NOT NULL,
  price INTEGER NOT NULL
);

CREATE TABLE Rates (
  id SERIAL PRIMARY KEY,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP,

  resource_id INTEGER NOT NULL,
  price INTEGER NOT NULL,
  FOREIGN KEY(resource_id) REFERENCES Resources(id)
);
