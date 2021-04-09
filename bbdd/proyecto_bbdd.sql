CREATE DATABASE db_proyecto;

USE db_proyecto;

CREATE TABLE profesor (
    id_profesor INTEGER(11) PRIMARY KEY AUTO_INCREMENT,
    numero INTEGER(5),
    nombre VARCHAR(150) NOT NULL,
    abreviacion VARCHAR(5),
    minhorasdia INTEGER(3),
    maxhorasdia INTEGER(3),
    departamento VARCHAR(50)
);

CREATE TABLE credencial (
    id_credencial INTEGER(11) PRIMARY KEY AUTO_INCREMENT,
    usuario VARCHAR(255) NOT NULL,
	passwd_hash VARCHAR(255),
    profesor INTEGER(11),
    rol VARCHAR(50),
    CONSTRAINT credencial_prof_fk FOREIGN KEY (profesor) REFERENCES profesor(id_profesor)
);

CREATE TABLE aula (
    id_aula INTEGER(11) PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(150) NOT NULL,
    planta INTEGER(3)
);