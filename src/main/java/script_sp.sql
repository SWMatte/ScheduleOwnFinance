CREATE DEFINER=`root`@`localhost` PROCEDURE `converter`()
BEGIN
     -- 1 condizione salvataggio 100% dell'entrata
    IF EXISTS (
        SELECT 1
        FROM registro_eventi
        WHERE saved_money = 1
          AND objective = 0
          AND percentage_save_money = 100
          AND triggered = 0
          AND type_event ='ENTRATA'
    ) THEN

        INSERT INTO totale_risparmiato (data, euro_risparmiati, registro_eventi_id)
        SELECT
            data,
            value AS euro_risparmiati,
            registro_eventi_id
        FROM
            registro_eventi
        WHERE
            saved_money = 1
            AND objective = 0
            AND percentage_save_money = 100
            AND triggered = 0
			AND type_event ='ENTRATA';


        -- Aggiorna il campo triggered di registro_eventi per gli eventi che sono stati inseriti in totale_risparmiato
UPDATE registro_eventi
SET
    triggered = TRUE
WHERE
    saved_money = 1 AND objective = 0
        AND percentage_save_money = 100
        AND triggered = 0
        AND type_event = 'ENTRATA'
        AND registro_eventi_id = (SELECT
            registro_eventi_id
        FROM
            (SELECT
                registro_eventi_id
            FROM
                registro_eventi
            WHERE
                saved_money = 1 AND objective = 0
                    AND percentage_save_money = 100
                    AND triggered = 0
                    AND type_event = 'ENTRATA') AS subquery);

    END IF;



     -- 2 condizione SPESA
    IF EXISTS (
        SELECT 1
        FROM registro_eventi re
        WHERE  re.type_event ='SPESA'
		 AND saved_money = 0
          AND objective = 0
          AND percentage_save_money = 0
          AND triggered = 0

    ) THEN   -- si inserisce il valore in maniera negativa nella gestione spese
    INSERT INTO gestione_spese (euro_disponibili, registro_eventi_id, euro_risparmiati_id)
    SELECT
        r.value * (-1) AS spesa,
        r.registro_eventi_id,
        null
       FROM registro_eventi r
        WHERE  r.type_event ='SPESA'
		 AND saved_money = 0
          AND objective = 0
          AND percentage_save_money = 0
          AND triggered = 0;


    -- aggiornamento del registro eventi in base alla spesa
UPDATE registro_eventi
SET
    triggered = TRUE
WHERE
    type_event = 'SPESA' AND saved_money = 0
        AND objective = 0
        AND percentage_save_money = 0
        AND triggered = 0
        AND registro_eventi_id = (SELECT
            registro_eventi_id
        FROM
            (SELECT
                registro_eventi_id
            FROM
                registro_eventi
            WHERE
                type_event = 'SPESA' AND saved_money = 0
                    AND objective = 0
                    AND percentage_save_money = 0
                    AND triggered = 0) AS subquery);

        END IF;



     -- 3  condizione il 100% che entra lo spendo
    IF EXISTS (
        SELECT 1
        FROM registro_eventi
        WHERE saved_money = 0
          AND objective = 0
          AND percentage_save_money = 0
          AND triggered = 0
          AND type_event ='ENTRATA'
    ) THEN
        --   Inserimento solo nella tabella gestione_spese
        INSERT INTO gestione_spese (euro_disponibili, registro_eventi_id, euro_risparmiati_id)
        SELECT
            value,
            registro_eventi_id,
            NULL
        FROM
            registro_eventi
        WHERE
            saved_money = 0
            AND objective = 0
            AND percentage_save_money = 0
            AND triggered = 0
            AND type_event ='ENTRATA';

-- Aggiorna il campo triggered per gli eventi che sono stati inseriti in gestione_spese

UPDATE registro_eventi
SET
    triggered = TRUE
WHERE
    saved_money = 0 AND objective = 0
        AND percentage_save_money = 0
        AND triggered = 0
        AND type_event = 'ENTRATA'
        AND registro_eventi_id = (SELECT
            registro_eventi_id
        FROM
            (SELECT
                registro_eventi_id
            FROM
                registro_eventi
            WHERE
                saved_money = 0 AND objective = 0
                    AND percentage_save_money = 0
                    AND triggered = 0
                    AND type_event = 'ENTRATA') AS subquery);
        END IF;


    --  4 VERIFICA RISPARMIO QUALCOSA


    IF EXISTS (
        SELECT 1
        FROM registro_eventi
        WHERE 	saved_money = 1
        AND objective = 0
        AND percentage_save_money < 100
		AND triggered = 0
        AND type_event ='ENTRATA'
    ) THEN

	INSERT INTO totale_risparmiato (data, euro_risparmiati, registro_eventi_id)
    SELECT
		data,
         (value * (percentage_save_money / 100)) AS euro_risparmiati,
        registro_eventi_id

    FROM
        registro_eventi
    WHERE
        saved_money = 1
        AND objective = 0
        AND percentage_save_money < 100
        AND triggered = 0
        AND type_event ='ENTRATA';


	INSERT INTO gestione_spese (euro_disponibili, registro_eventi_id, euro_risparmiati_id)
    SELECT
        r.value -  (value * (percentage_save_money / 100))AS euro_spendibili,
        r.registro_eventi_id,
        tr.euro_risparmiati_id
    FROM
        registro_eventi r
    INNER JOIN totale_risparmiato tr
        ON r.registro_eventi_id = tr.registro_eventi_id
    WHERE
        saved_money = 1
        AND objective = 0
        AND percentage_save_money < 100
        AND triggered = 0
        AND type_event ='ENTRATA';


-- aggiornamento registro eventi a seguito della condizione risparmio qualcosa
UPDATE registro_eventi
SET
    triggered = TRUE
WHERE
    saved_money = 1 AND objective = 0
        AND percentage_save_money < 100
        AND triggered = 0
        AND type_event = 'ENTRATA'
        AND registro_eventi_id = (SELECT
            registro_eventi_id
        FROM
            (SELECT
                registro_eventi_id
            FROM
                registro_eventi
            WHERE
                saved_money = 1 AND objective = 0
                    AND percentage_save_money < 100
                    AND triggered = 0
                    AND type_event = 'ENTRATA') AS subquery);

       END IF;


END


-- seconda store procedure
CREATE DEFINER=`root`@`localhost` PROCEDURE `Finanza_disponibile`(out totale double)
BEGIN
    DECLARE total_amount DOUBLE;
	SET total_amount = 0;
    SELECT SUM(euro_disponibili) INTO total_amount
    FROM gestione_spese;
    IF total_amount IS NULL THEN
        SET totale = 0;
    ELSE
        SET totale = total_amount;
    END IF;
END

CREATE DEFINER=`root`@`localhost` PROCEDURE `gestione_debito`(IN input_debito_id INT)
BEGIN
     SET SQL_SAFE_UPDATES = 0;

     IF EXISTS (
        SELECT 1
        FROM registro_eventi
        WHERE saved_money = 0
          AND objective = 1
          AND percentage_save_money = 0
          AND triggered = 0
          AND type_event = 'ENTRATA'
    ) THEN
         INSERT INTO debito_rateizzato_history (euro_dedicati, debito_id)
        SELECT
            value AS euro_dedicati,
            input_debito_id AS debito_id
        FROM
            registro_eventi
        WHERE
            saved_money = 0
            AND objective = 1
            AND percentage_save_money = 0
            AND triggered = 0
            AND type_event = 'ENTRATA';

        -- Aggiorna il campo triggered
        UPDATE registro_eventi
        SET triggered = TRUE
        WHERE saved_money = 0
            AND objective = 1
            AND percentage_save_money = 0
            AND triggered = 0
            AND type_event = 'ENTRATA';
    END IF;

         UPDATE debito_rateizzato dr
        JOIN (
            SELECT
              euro_dedicati,
              debito_id
			FROM
                debito_rateizzato_history

         ) AS history
        ON dr.debito_id = history.debito_id
        SET dr.valore_corrente = dr.valore_corrente - history.euro_dedicati;
     SET SQL_SAFE_UPDATES = 1;
END
-- QUERY VISTA
CREATE OR REPLACE VIEW riepilogo AS
SELECT
    re.description,
    re.data,
    re.type_event,
    re.value,
    t.euro_risparmiati,
    gs.euro_disponibili,
    -- Calcolo della percentuale di risparmio
    CASE
        WHEN re.value > 0 THEN (t.euro_risparmiati / re.value) * 100
        ELSE 0
    END AS percentuale_risparmio
FROM
    registro_eventi re
INNER JOIN
    totale_risparmiato t
    ON re.registro_eventi_id = t.registro_eventi_id
INNER JOIN
    gestione_spese gs
    ON re.registro_eventi_id = gs.registro_eventi_id;
