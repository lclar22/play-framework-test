# --- !Ups

create table "people" (
  "id" bigint generated by default as identity(start with 1) not null primary key,
  "name" varchar not null,
  "last_name" varchar not null,
  "age" int not null
);

create table "clientes" (
  "id" bigint generated by default as identity(start with 1) not null primary key,
  "nombre" varchar not null,
  "carnet" int not null,
  "id_asociacion" int not null
);

create table "asociacion" (
  "id" bigint generated by default as identity(start with 1) not null primary key,
  "nombre" varchar not null
);

create table "modulo" (
  "id" bigint generated by default as identity(start with 1) not null primary key,
  "nombre" varchar not null,
  "president" bigint
);

create table "account" (
  "id" bigint generated by default as identity(start with 1) not null primary key,
  "numero" int not null,
  "banco" varchar not null,
  "monto" int not null
);

create table "transaction" (
  "id" bigint generated by default as identity(start with 1) not null primary key,
  "date" varchar not null,
  "type" varchar,
  "descripcion" varchar
);

create table "transactionDetail" (
  "id" bigint generated by default as identity(start with 1) not null primary key,
  "transaction" bigint,
  "account" bigint,
  "amount" double
);

create table "bancos" (
  "id" bigint generated by default as identity(start with 1) not null primary key,
  "nombre" varchar not null,
  "tipo" varchar not null,
  "currentMoney" varchar,
  "typeMoney" varchar
);

create table "product" (
  "id" bigint generated by default as identity(start with 1) not null primary key,
  "nombre" varchar not null,
  "costo" int,
  "porcentage" int,
  "descripcion" varchar,
  "unidad" bigint,
  "currentAmount" int
);

create table "productor" (
  "id" bigint generated by default as identity(start with 1) not null primary key,
  "nombre" varchar not null,
  "carnet" int not null,
  "telefono" int,
  "direccion" varchar,
  "account" bigint,
  "asociacion" bigint,
  "modulo" bigint,
  "totalDebt" double,
  "numberPayment" int,
  "position" varchar
);

create table "proveedor" (
  "id" bigint generated by default as identity(start with 1) not null primary key,
  "nombre" varchar not null,
  "telefono" int,
  "direccion" varchar,
  "contacto" varchar,
  "account" bigint
);

create table "reportes" (
  "id" bigint generated by default as identity(start with 1) not null primary key,
  "monto" int not null,
  "account" int not null,
  "cliente" int not null
);

create table "user" (
  "id" bigint generated by default as identity(start with 1) not null primary key,
  "nombre" varchar not null,
  "carnet" int not null,
  "telefono" int,
  "direccion" varchar,
  "sueldo" int,
  "type" varchar
);

create table "productRequest" (
  "id" bigint generated by default as identity(start with 1) not null primary key,
  "date" varchar,
  "veterinario" bigint,
  "storekeeper" bigint,
  "status" varchar,
  "detail" varchar
);

create table "requestRow" (
  "id" bigint generated by default as identity(start with 1) not null primary key,
  "requestId" bigint,
  "productId" bigint,
  "productorId" bigint,
  "quantity" int,
  "precio" double,
  "paid" int,
  "debt" int,
  "cuota" int,
  "status" varchar
);

create table "requestRowProductor" (
  "id" bigint generated by default as identity(start with 1) not null primary key,
  "requestId" bigint,
  "productId" bigint,
  "productorId" bigint,
  "quantity" int,
  "precio" int,
  "paid" int,
  "debt" int,
  "cuota" int,
  "status" varchar,
  "type" varchar
);

create table "productInv" (
  "id" bigint generated by default as identity(start with 1) not null primary key,
  "productId" bigint,
  "proveedorId" bigint,
  "amount" int,
  "amountLeft" int,
  "cost_unit" int,
  "total_cost" int
);

create table "debt" (
  "id" bigint generated by default as identity(start with 1) not null primary key,
  "total" int,
  "paid" int,
  "status" varchar,
  "producto_id" bigint
);

create table "discountReport" (
  "id" bigint generated by default as identity(start with 1) not null primary key,
  "startDate" varchar,
  "endDate" varchar,
  "status" varchar,
  "total" int,
  "user_id" bigint
);

create table "discountDetail" (
  "id" bigint generated by default as identity(start with 1) not null primary key,
  "discountReport" bigint,
  "requestRow" bigint,
  "productorId" bigint,
  "status" varchar,
  "discount" Int
);

# --- !Downs
drop table "people" if exists;
drop table "clientes" if exists;
drop table "asociacion" if exists;
drop table "modulo" if exists;
drop table "account" if exists;
drop table "transaction" if exists;
drop table "productor" if exists;
drop table "proveedor" if exists;
drop table "reportes" if exists;
drop table "bancos" if exists;
drop table "product" if exists;
drop table "user" if exists;
drop table "productInv" if exists;
drop table "discountReport" if exists;
drop table "discountDetail" if exists;
drop table "requestRow" if exists;
drop table "productRequest" if exists;
drop table "requestRowProductor" if exists;
