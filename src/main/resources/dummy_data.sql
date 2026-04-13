DECLARE @i INT = 1;
DECLARE @john_uuid UNIQUEIDENTIFIER = '715db132-012c-47b8-86c8-a169853eaae3';
DECLARE @jane_uuid UNIQUEIDENTIFIER = '31f3ae79-2254-4d97-986a-691abc4f7292';

DECLARE @john_savings UNIQUEIDENTIFIER = 'c6ba0062-e9fb-4da0-a125-b618b7705ad1';
DECLARE @john_current UNIQUEIDENTIFIER = '25ea415c-29aa-4174-8f43-2462313599dd';
DECLARE @jane_savings UNIQUEIDENTIFIER = '0de7cab1-e12c-40ed-9a63-87b3ec10c41f';

-- Arrays of possible values
DECLARE @currencies TABLE (ccy VARCHAR(3), rate DECIMAL(19,6));
INSERT INTO @currencies VALUES ('USD', 4.3250), ('EUR', 4.6000), ('SGD', 3.5000), ('GBP', 6.0000), ('AUD', 2.8500), ('JPY', 0.0285);

DECLARE @recipients TABLE (acct VARCHAR(50), name VARCHAR(100));
INSERT INTO @recipients VALUES
                            ('RECIPIENT_001', 'Ahmad Ali'),
                            ('RECIPIENT_002', 'Siti Omar'),
                            ('RECIPIENT_003', 'Muthu Raj'),
                            ('RECIPIENT_004', 'Wong Fei Hung'),
                            ('RECIPIENT_005', 'Kumaresan A/L Muthu'),
                            ('RECIPIENT_006', 'Tan Ah Kow'),
                            ('RECIPIENT_007', 'Leong Bee Leng'),
                            ('RECIPIENT_008', 'Rajendran A/L Krishnan'),
                            ('RECIPIENT_009', 'Lim Siew Mei'),
                            ('RECIPIENT_010', 'Ismail Bin Abdullah');

DECLARE @statuses TABLE (status VARCHAR(20), weight INT);
INSERT INTO @statuses VALUES ('SCHEDULED', 70), ('COMPLETED', 20), ('CANCELLED', 10);

-- Generate 200 transactions
WHILE @i <= 200
BEGIN
    -- Randomly assign customer (60% John, 40% Jane)
    DECLARE @cust_uuid UNIQUEIDENTIFIER;
    DECLARE @acct_uuid UNIQUEIDENTIFIER;
    DECLARE @rand_cust INT = 1 + (RAND() * 10);

    IF @rand_cust <= 6  -- 60% John
BEGIN
        SET @cust_uuid = @john_uuid;
        -- Alternate between savings and current for John
        IF @i % 2 = 0
            SET @acct_uuid = @john_savings;
ELSE
            SET @acct_uuid = @john_current;
END
ELSE  -- 40% Jane
BEGIN
        SET @cust_uuid = @jane_uuid;
        SET @acct_uuid = @jane_savings;
END

    -- Random amount between 50 and 10000
    DECLARE @orig_amt DECIMAL(15,2) = 50 + (RAND() * 9950);
    DECLARE @orig_amt_rounded DECIMAL(15,2) = ROUND(@orig_amt / 10, 0) * 10;

    -- Random currency and rate
    DECLARE @ccy VARCHAR(3);
    DECLARE @rate DECIMAL(19,6);
    DECLARE @rand_ccy INT = 1 + (RAND() * 6);

    IF @rand_ccy = 1
SELECT @ccy = 'USD', @rate = 4.3250;
ELSE IF @rand_ccy = 2
SELECT @ccy = 'EUR', @rate = 4.6000;
ELSE IF @rand_ccy = 3
SELECT @ccy = 'SGD', @rate = 3.5000;
ELSE IF @rand_ccy = 4
SELECT @ccy = 'GBP', @rate = 6.0000;
ELSE IF @rand_ccy = 5
SELECT @ccy = 'AUD', @rate = 2.8500;
ELSE
SELECT @ccy = 'JPY', @rate = 0.0285;

-- Calculate converted amount
DECLARE @conv_amt DECIMAL(15,2) = @orig_amt_rounded * @rate;

    -- Random status (70% SCHEDULED, 20% COMPLETED, 10% CANCELLED)
    DECLARE @status VARCHAR(20);
    DECLARE @rand_status INT = 1 + (RAND() * 100);

    IF @rand_status <= 70
        SET @status = 'SCHEDULED';
ELSE IF @rand_status <= 90
        SET @status = 'COMPLETED';
ELSE
        SET @status = 'CANCELLED';

    -- Random date between 90 days ago and 180 days from now
    DECLARE @days_offset INT = -90 + (RAND() * 270);
    DECLARE @sched_date DATE = DATEADD(day, @days_offset, GETDATE());

    -- Random recipient
    DECLARE @recipient_acct VARCHAR(50) = 'RECIPIENT_' + RIGHT('000' + CAST(@i AS VARCHAR), 3);
    DECLARE @recipient_name VARCHAR(100) = 'Recipient ' + CAST(@i AS VARCHAR);

INSERT INTO TRX_DUITLATER (
    TRX_UUID, CUST_UUID, ACCT_UUID, IDEMP_KEY,
    ORIG_AMT, ORIG_CCY, CONV_AMT, CONV_CCY, EXCH_RATE,
    DT_SCHED, TO_ACCT_NBR, TO_CUST_NAME, STS_CD,
    DT_CRT, DT_UPD
) VALUES (
             NEWID(), @cust_uuid, @acct_uuid, NEWID(),
             @orig_amt_rounded, @ccy, @conv_amt, 'MYR', @rate,
             @sched_date, @recipient_acct, @recipient_name, @status,
             GETDATE(), GETDATE()
         );

SET @i = @i + 1;

    -- Progress indicator every 50 records
    IF @i % 50 = 0
BEGIN
        PRINT 'Inserted ' + CAST(@i AS VARCHAR) + ' records...';
END
END
GO