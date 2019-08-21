package com.wirelessiths.dal;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@DynamoDBTable(tableName = "PLACEHOLDER_BOOKINGS_TABLE_NAME")
public class Booking {

    // get the table name from env. var. set in serverless.yml
    private static final String BOOKINGS_TABLE_NAME = System.getenv("BOOKINGS_TABLE_NAME");

    private static DynamoDBAdapter db_adapter;
    private final AmazonDynamoDB client;
    private final DynamoDBMapper mapper;

    private Logger logger = Logger.getLogger(this.getClass());

    private String bookingId;
    private String scooterId;
    private String userId;
    private String message;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @DynamoDBHashKey(attributeName = "bookingId")
    @DynamoDBAutoGeneratedKey
    public String getBookingId() {
        return bookingId;
    }
    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }


    @DynamoDBAttribute(attributeName = "scooterId")
    public String getScooterId() {
        return this.scooterId;
    }
    public void setScooterId(String scooterId) {
        this.scooterId = scooterId;
    }

    @DynamoDBRangeKey(attributeName = "message")
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }


    @DynamoDBIndexHashKey(attributeName = "userId", globalSecondaryIndexName = "nameIndex")
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }



    @DynamoDBTypeConverted( converter = LocalDateTimeConverter.class )
    @DynamoDBIndexHashKey(attributeName = "startTime", globalSecondaryIndexName = "timeIndex")
    public LocalDateTime getStartTime() {
        return startTime;
    }
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @DynamoDBTypeConverted( converter = LocalDateTimeConverter.class )
    @DynamoDBIndexRangeKey(attributeName = "endTime", globalSecondaryIndexName = "timeIndex")
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }


    public Booking() {
        // build the mapper config
        DynamoDBMapperConfig mapperConfig = DynamoDBMapperConfig.builder()
                .withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(BOOKINGS_TABLE_NAME))
                .build();
        // get the db adapter
        this.db_adapter = DynamoDBAdapter.getInstance();
        this.client = this.db_adapter.getDbClient();
        // create the mapper with config
        this.mapper = this.db_adapter.createDbMapper(mapperConfig);
    }

    @Override
    public String toString() {
        return "Booking{" +
                ", scooterId='" + scooterId + '\'' +
                ", userId='" + userId + '\'' +
                ", message='" + message + '\'' +
//                ", startTime=" + startTime +
//                ", endTime=" + endTime +
                '}';
    }

    // methods
    public Boolean ifTableExists() {
        return this.client.describeTable(BOOKINGS_TABLE_NAME).getTable().getTableStatus().equals("ACTIVE");
    }

    public List<Booking> list() throws IOException {
        DynamoDBScanExpression scanExp = new DynamoDBScanExpression();
        List<Booking> results = this.mapper.scan(Booking.class, scanExp);
        for (Booking p : results) {
            logger.info("Booking - list(): " + p.toString());
        }
        return results;
    }

    public Booking get(String id) throws IOException {
        Booking user = null;

        HashMap<String, AttributeValue> av = new HashMap<String, AttributeValue>();
        av.put(":v1", new AttributeValue().withS(id));

        DynamoDBQueryExpression<Booking> queryExp = new DynamoDBQueryExpression<Booking>()
                .withKeyConditionExpression("bookingId = :v1")
                .withExpressionAttributeValues(av);

        PaginatedQueryList<Booking> result = this.mapper.query(Booking.class, queryExp);
        if (result.size() > 0) {
            user = result.get(0);
            logger.info("Booking - get(): booking - " + user.toString());
        } else {
            logger.info("Booking - get(): booking - Not Found.");
        }
        return user;
    }

    public void save(Booking booking) throws IOException {
        logger.info("Booking - save(): " + booking.toString());
        this.mapper.save(booking);
    }

    public Boolean delete(String id) throws IOException {
        Booking booking = null;
        // get product if exists
        booking = get(id);
        if (booking != null) {
            logger.info("Booking - delete(): " + booking.toString());
            this.mapper.delete(booking);
        } else {
            logger.info("Booking - delete(): booking - does not exist.");
            return false;
        }
        return true;
    }

}
