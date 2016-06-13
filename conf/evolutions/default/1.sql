
# --- !Downs
drop table asociacion;
drop table module;
drop table account;
drop table transaction;
drop table transactionDetail;
drop table productor;
drop table proveedor;
drop table reportes;
drop table bancos;
drop table product;
drop table user;
drop table productInv;
drop table discountReport;
drop table discountDetail;
drop table requestRow;
drop table productRequest;
drop table requestRowProductor;
drop table logEntry;

# --- !Ups

create table asociacion (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(30) not null
);

create table module (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(30) not null,
  president INT,
  description VARCHAR(30),
  asociacion INT(6),
  asociacionName VARCHAR(30)

);

create table unitMeasure (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(30),
  quantity INT(6),
  description VARCHAR(30)
);

create table account (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(30),
  name VARCHAR(30),
  type VARCHAR(30),
  parent INT(6),
  negativo VARCHAR(30),
  description VARCHAR(30),
  child boolean,
  debit double,
  credit double,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table transaction (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  date VARCHAR(30),
  type VARCHAR(30),
  description VARCHAR(30),
  createdBy INT,
  createdBy_name VARCHAR(30),
  autorizedBy INT,
  autorizedBy_name VARCHAR(30), 
  recievedBy INT,
  recievedBy_name VARCHAR(30), 
  updatedBy INT(6),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table logEntry (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  date VARCHAR(30),
  action VARCHAR(30),
  tableName_1 VARCHAR(30),
  userId INT(6)
);

create table transactionDetail (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  transaction INT,
  account INT,
  debit double,
  credit double,
  transactionDate VARCHAR(30),
  accountCode VARCHAR(30),
  accountName VARCHAR(30),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  createdBy INT,
  createdBy_name VARCHAR(30)
);

create table bancos (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(30) not null,
  tipo VARCHAR(30) not null,
  currentMoney VARCHAR(30),
  typeMoney VARCHAR(30)
);

create table product (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(30) not null,
  cost double,
  percent double,
  price double,
  descripcion VARCHAR(30),
  unitMeasure INT,
  unitMeasureName VARCHAR(30),
  currentAmount int,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table productor (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(30) not null,
  carnet int not null,
  telefono int,
  direccion VARCHAR(30),
  account VARCHAR(30),
  module INT,
  moduleName VARCHAR(30),
  asociacionName VARCHAR(30),
  totalDebt double,
  numberPayment int,
  position VARCHAR(30),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table proveedor (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(30) not null,
  telefono int,
  direccion VARCHAR(30),
  contacto VARCHAR(30),
  account INT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table reportes (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  monto int not null,
  account int not null,
  cliente int not null,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table user (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(30) not null,
  carnet int not null,
  telefono int,
  direccion VARCHAR(30),
  sueldo int,
  type VARCHAR(30),
  login VARCHAR(30),
  password VARCHAR(30),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table productRequest (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  date VARCHAR(30),
  veterinario INT,
  veterinarioname VARCHAR(30),
  storekeeper INT,
  storekeeperName VARCHAR(30),
  user INT,
  userName VARCHAR(30),
  module INT,
  moduleName VARCHAR(30),
  status VARCHAR(30),
  detail VARCHAR(30),
  type VARCHAR(30),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  createdBy INT,
  createdBy_name VARCHAR(30)
);

create table requestRow (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  requestId INT,
  productId INT,
  productName VARCHAR(30),
  productorId INT,
  productorName VARCHAR(30),
  quantity INT,
  precio double,
  unitMeasure INT,
  unitMeasureName VARCHAR(30),
  paid INT,
  debt INT,
  cuota INT,
  status VARCHAR(30),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  createdBy INT,
  createdBy_name VARCHAR(30)
);

create table requestRowProductor (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  requestRowId INT,
  productId INT,
  productName VARCHAR(30),
  productorId INT,
  productorName VARCHAR(30),
  quantity int,
  precio int,
  paid int,
  debt int,
  cuota int,
  status VARCHAR(30),
  type VARCHAR(30),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  createdBy INT,
  createdBy_name VARCHAR(30)
);

create table productInv (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  productId INT,
  proveedorId INT,
  amount int,
  amountLeft int,
  cost_unit double,
  total_cost double,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table discountReport (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  startDate VARCHAR(30),
  endDate VARCHAR(30),
  status VARCHAR(30),
  total double,
  user_id INT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table discountDetail (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  discountReport INT,
  requestRow INT,
  productorId INT,
  status VARCHAR(30),
  discount double
);
