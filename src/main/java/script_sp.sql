CREATE DEFINER=`root`@`localhost` PROCEDURE `converter`()
BEGIN
SET SQL_SAFE_UPDATES = 0;

    -- Verifica se ci sono record da elaborare per la prima parte
    IF EXISTS (
        SELECT 1
        FROM registro_eventi
        WHERE saved_money = 1
          AND objective = 0
          AND percentage_save_money = 100
          AND triggered = 0
          AND type_event ='ENTRATA'
    ) THEN
        -- 0 Gestione caso quando il 100% del valore deve essere risparmiato
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


        -- Aggiorna il campo triggered per gli eventi che sono stati inseriti in totale_risparmiato
        UPDATE registro_eventi
        SET triggered = TRUE
        WHERE saved_money = 1
          AND objective = 0
          AND percentage_save_money = 100
          AND triggered = 0
          AND type_event ='ENTRATA';

    END IF;

  -- Verifica se ci sono record da elaborare per la seconda parte
    IF EXISTS (
        SELECT 1
        FROM registro_eventi
        WHERE saved_money = 0
          AND objective = 0
          AND percentage_save_money = 0
          AND triggered = 0
          AND type_event ='ENTRATA'
    ) THEN
        -- 2 Inserimento solo nella tabella gestione_spese
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
        SET triggered = TRUE
        WHERE saved_money = 0
          AND objective = 0
          AND percentage_save_money = 0
          AND triggered = 0
          AND type_event ='ENTRATA';
    END IF;


    IF EXISTS (
        SELECT 1
        FROM registro_eventi
        WHERE   type_event ='SPESA'
    ) THEN
    INSERT INTO gestione_spese (euro_disponibili, registro_eventi_id, euro_risparmiati_id)
    SELECT
        r.value * (-1) AS spesa,
        r.registro_eventi_id,
        null
    FROM
        registro_eventi r
    WHERE
     type_event ='SPESA';

	UPDATE registro_eventi
    SET triggered = TRUE
    WHERE type_event ='SPESA';
    END IF;

    --


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
		objective = 0
        AND percentage_save_money < 100
        AND triggered = 0
        AND type_event ='ENTRATA';

   UPDATE registro_eventi
    SET triggered = TRUE
    WHERE saved_money = 1
      AND objective = 0
      AND percentage_save_money < 100
      AND type_event ='ENTRATA';
    END IF;
SET SQL_SAFE_UPDATES = 1;


END


-- second store procedure
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
