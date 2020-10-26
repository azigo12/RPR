BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "drzava" (
	"id"	INTEGER,
	"naziv"	TEXT,
	"glavni_grad"	INTEGER,
	PRIMARY KEY("id")
);
CREATE TABLE IF NOT EXISTS "grad" (
	"id"	INTEGER,
	"naziv"	TEXT,
	"broj_stanovnika"	INTEGER,
	"drzava"	INTEGER,
	"slika" TEXT,
	"postanski_broj" INTEGER,
	PRIMARY KEY("id")
);
INSERT INTO "drzava" VALUES (1,'Francuska',1);
INSERT INTO "drzava" VALUES (2,'Velika Britanija',2);
INSERT INTO "drzava" VALUES (3,'Austrija',3);
INSERT INTO "grad" VALUES (1,'Pariz',2206488,1,'C:\Users\Amila\Desktop\slikeGradova\Pariz.jpg',71000);
INSERT INTO "grad" VALUES (2,'London',8825000,2, 'C:\Users\Amila\Desktop\slikeGradova\London.jpg', 71000);
INSERT INTO "grad" VALUES (3,'Beč',1899055,3, 'C:\Users\Amila\Desktop\slikeGradova\Bech.jpg',71000);
INSERT INTO "grad" VALUES (4,'Manchester',545500,2, 'C:\Users\Amila\Desktop\slikeGradova\Manchester.jpg',71000);
INSERT INTO "grad" VALUES (5,'Graz',280200,3,'C:\Users\Amila\Desktop\slikeGradova\Graz.jpg',71000);
COMMIT;