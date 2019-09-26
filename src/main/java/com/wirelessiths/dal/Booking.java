package com.wirelessiths.dal;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import com.wirelessiths.dal.trip.Trip;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;


/**
 *This class contains the variables required in a booking.
 *It also creates a database client in AWSDynamoDb and takes advantage of the AutoGeneratedKey function.
 */

@DynamoDBTable(tableName = "PLACEHOLDER_BOOKINGS_TABLE_NAME")
public class Booking {

    // get the table name from .env. var. set in serverless.yml
    private static final String BOOKINGS_TABLE_NAME = System.getenv("BOOKINGS_TABLE_NAME");

    private String type;
    private String scooterId;
    private String bookingId;
    private String userId;

    private Instant startTime;
    private Instant endTime;
    //private LocalDate bookingDate;
    private LocalDate startDate;
    private LocalDate endDate;

    private BookingStatus bookingStatus;

    private List<Trip> trips = new ArrayList<>();


    private static DynamoDBAdapter db_adapter;
    private final AmazonDynamoDB client;
    private final DynamoDBMapper mapper;
    private final DynamoDB dynamoDB;

    private final LoggerAdapter logger;
    private final StringBuilder sb = new StringBuilder();

   /**
     *This method connects to DynamoDB, creates a table with a mapperConfig.
     */
    public Booking() {
        // build the mapper config
        DynamoDBMapperConfig mapperConfig = DynamoDBMapperConfig.builder()
                .withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(BOOKINGS_TABLE_NAME))
                .build();
        // get the db adapter
        this.db_adapter = DynamoDBAdapter.getInstance();
        this.client = this.db_adapter.getDbClient();
        this.dynamoDB = this.db_adapter.getDynamoDB();
        // create the mapper with config
        this.mapper = this.db_adapter.createDbMapper(mapperConfig);

        this.logger = new LoggerAdapter(LogManager.getLogger(this.getClass()));
    }

    public Booking(AmazonDynamoDB client, DynamoDBMapperConfig config){
        this.client = client;
        this.dynamoDB = new DynamoDB(client);
        this.mapper = new DynamoDBMapper(client, config);
        this.logger = new LoggerAdapter();
        //this.logger = LogManager.getLogger(this.getClass());
    }


    @DynamoDBHashKey(attributeName = "scooterId")
    public String getScooterId() {
        return this.scooterId;
    }
    public void setScooterId(String scooterId) {
        this.scooterId = scooterId;
    }

    //@JsonFormat(pattern = "yyyy-MM-dd T HH:mm:ss", timezone = "UTC")
    @DynamoDBRangeKey(attributeName = "endTime")
    //@DynamoDBAttribute(attributeName = "endTime")
    @DynamoDBIndexRangeKey(attributeName = "endTime", globalSecondaryIndexName = "endTimeIndex")
    @DynamoDBTypeConverted( converter = InstantConverter.class )
    public Instant getEndTime() {
        return endTime;
    }
    public void setEndTime(Instant endTime) {
        this.endDate = LocalDate.parse(endTime.toString().split("T")[0]);
        this.endTime = endTime;
    }

    @DynamoDBIndexHashKey(attributeName = "endDate", globalSecondaryIndexName = "endTimeIndex")
    @DynamoDBTypeConverted( converter = LocalDateConverter.class )
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }


    @DynamoDBIndexRangeKey(attributeName = "startTime", globalSecondaryIndexNames = {"bookingIndex", "startTimeIndex"})
    @DynamoDBTypeConverted( converter = InstantConverter.class )
    public Instant getStartTime() {
        return startTime;
    }
    public void setStartTime(Instant startTime) {
        this.startDate = LocalDate.parse(startTime.toString().split("T")[0]);
        this.startTime = startTime;
    }

    @DynamoDBIndexHashKey(attributeName = "startDate", globalSecondaryIndexName = "startTimeIndex")
    @DynamoDBTypeConverted( converter = LocalDateConverter.class )
    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    @DynamoDBIndexHashKey(attributeName = "bookingId", globalSecondaryIndexName = "bookingIndex")
    @DynamoDBAutoGeneratedKey
    public String getBookingId() {
        return bookingId;
    }
    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    @DynamoDBIndexHashKey(attributeName = "userId", globalSecondaryIndexName = "userIndex")
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @DynamoDBTypeConvertedEnum
    @DynamoDBAttribute(attributeName="bookingStatus")
    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    @DynamoDBAttribute(attributeName = "trips")
    public List<Trip> getTrips() {
        return trips;
    }

    //@JsonSetter("trip_overview_list")
    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "scooterId='" + scooterId + '\'' +
                ", bookingId='" + bookingId + '\'' +
                ", userId='" + userId + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", bookingStatus=" + bookingStatus +
                ", trips=" + trips +
                '}';
    }

    public List<Booking> validateBooking(Booking booking, int maxDuration, int buffer) throws IOException{

        String start = booking.getStartTime().minusSeconds(buffer).toString();
        String end = booking.getEndTime().plusSeconds(buffer).toString();
        String endPlusMaxDur = booking.getEndTime().plusSeconds(maxDuration).toString();

        Map<String, AttributeValue> values = new HashMap<>();
        values.put(":id", new AttributeValue().withS(booking.getScooterId()));
        values.put(":start", new AttributeValue().withS(start));
        values.put(":endPlusMaxDur", new AttributeValue().withS(endPlusMaxDur));
        values.put(":end", new AttributeValue().withS(end));
        values.put(":invalidState", new AttributeValue().withS(BookingStatus.CANCELLED.toString()));
        values.put(":invalidState2", new AttributeValue().withS(BookingStatus.COMPLETED.toString()));

        DynamoDBQueryExpression<Booking> queryExp = new DynamoDBQueryExpression<>();

        queryExp.withKeyConditionExpression("scooterId = :id and endTime between :start and :endPlusMaxDur")
                .withExpressionAttributeValues(values)
                .withConsistentRead(true)
                //.withFilterExpression("startTime < :end AND bookingStatus = :validState")//Todo: add bookingState to range key, for more effective querying?
                .withFilterExpression("startTime < :end AND bookingStatus <> :invalidState AND bookingStatus <> :invalidState2");

        return mapper.query(Booking.class, queryExp);
    }


    public Boolean ifTableExists() {
        System.out.println("i iftabelexists");

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
        Booking booking = null;

        HashMap<String, AttributeValue> av = new HashMap<String, AttributeValue>();
        av.put(":v1", new AttributeValue().withS(id));

        DynamoDBQueryExpression<Booking> queryExp = new DynamoDBQueryExpression<Booking>()
                .withKeyConditionExpression("bookingId = :v1")
                .withExpressionAttributeValues(av)
                .withConsistentRead(false);
        queryExp.setIndexName("bookingIndex");

        PaginatedQueryList<Booking> result = this.mapper.query(Booking.class, queryExp);
        if (!result.isEmpty()) {
            booking = result.get(0);
            logger.info("Booking - get(): booking - " + booking.toString());
        } else {
            logger.info("Booking - get(): booking - Not Found.");
        }
        return booking;
    }


    //get bookings by startTime that has passed from now minus deadline
    public List<Booking> bookingsByStartTime(int deadlineSeconds){
        Instant startCheck = Instant.now().minusSeconds(deadlineSeconds);
        LocalDate date = LocalDate.parse(startCheck.toString().split("T")[0]);

        Map<String, AttributeValue> values = new HashMap<>();
        values.put(":today", new AttributeValue().withS(date.toString()));
        values.put(":start1", new AttributeValue().withS(startCheck.minusSeconds(60).toString()));
        values.put(":start2", new AttributeValue().withS(startCheck.toString()));
        values.put(":validState", new AttributeValue().withS(BookingStatus.VALID.toString()));

        DynamoDBQueryExpression<Booking> queryExp = new DynamoDBQueryExpression<>();
        queryExp.withKeyConditionExpression("startDate = :today and startTime between :start1 and :start2")
                .withFilterExpression("bookingStatus = :validState")


                .withIndexName("startTimeIndex")
                .withExpressionAttributeValues(values)
                .withConsistentRead(false);
        return mapper.query(Booking.class, queryExp);

    }


    //return all bookings that has ended (now-6) to (now-5) minutes ago and that is not in a cancelled state
    public List<Booking> bookingsByEndTime(){
        //start-value to check for bookings ending from 6 to 5 minutes back from now
        Instant startCheck = Instant.now().minusSeconds(60 * 5L);
        //we need a startcheck date to use with the gsi endTimeIndex hash key
        LocalDate date = LocalDate.parse(startCheck.toString().split("T")[0]);

        Map<String, AttributeValue> values = new HashMap<>();
        values.put(":today", new AttributeValue().withS(date.toString()));
        values.put(":end1", new AttributeValue().withS(startCheck.minusSeconds(60).toString()));
        values.put(":end2", new AttributeValue().withS(startCheck.toString()));
        values.put(":invalidState", new AttributeValue().withS(BookingStatus.CANCELLED.toString()));

        DynamoDBQueryExpression<Booking> queryExp = new DynamoDBQueryExpression<>();
        queryExp.withKeyConditionExpression("endDate = :today and endTime between :end1 and :end2")
                .withFilterExpression("bookingStatus <> :invalidState")
                .withIndexName("endTimeIndex")
                .withExpressionAttributeValues(values)
                .withConsistentRead(false);
        return mapper.query(Booking.class, queryExp);
    }

    public List<Booking> bookingsByUserId(String userId) throws IOException {
       return bookingsByUserId(userId, null);
    }

    public List<Booking> bookingsByUserId(String userId, Map<String, String> filter) throws IOException {

        Booking booking = new Booking();
        booking.setUserId(userId);

        if(!Optional.ofNullable(filter).isPresent()){
            DynamoDBQueryExpression<Booking> queryExpression =
                    new DynamoDBQueryExpression<>();
            queryExpression.setHashKeyValues(booking);
            queryExpression.setIndexName("userIndex");
            queryExpression.setConsistentRead(false);

            return mapper.query(Booking.class, queryExpression);
        }
        Map<String, AttributeValue> values = new HashMap<>();
        filter.forEach((s1, s2) -> values.put(":"+s1, new AttributeValue().withS(s2)));
        StringBuilder filterExpression = new StringBuilder();

        values.forEach((v1, v2) -> {
            // Don't put an "and" the first time
            if (!filterExpression.toString().isEmpty()) {
                filterExpression.append(" and ");
            }
            filterExpression.append(v1.substring(1)).append(" = ").append(v1);
        });

        DynamoDBQueryExpression<Booking> queryExpression =
                new DynamoDBQueryExpression<>();
        queryExpression
        .withExpressionAttributeValues(values)
        .withHashKeyValues(booking)
        .withFilterExpression(filterExpression.toString())
        .withIndexName("userIndex")
        .withConsistentRead(false);

        return mapper.query(Booking.class, queryExpression);
    }


    public List<Booking> bookingsByScooterId(String scooterId) throws IOException {
        return bookingsByScooterId(scooterId, null);
    }

    public List<Booking> bookingsByScooterId(String scooterId, Map<String, String> filter) throws IOException {

        Map<String, AttributeValue> values = new HashMap<>();

        if(!Optional.ofNullable(filter).isPresent()){
            values.put(":v1", new AttributeValue().withS(scooterId));
            DynamoDBQueryExpression<Booking> queryExp =
                    new DynamoDBQueryExpression<>();
            queryExp.withKeyConditionExpression("scooterId = :v1")
                    .withExpressionAttributeValues(values)
                    .withConsistentRead(true);

            return mapper.query(Booking.class, queryExp);
        }
        else {
            filter.forEach((s1, s2) -> values.put(":" + s1, new AttributeValue().withS(s2)));
            StringBuilder filterExpression = new StringBuilder();
            Map<String, String> expression = new HashMap<>();
            values.forEach((v1, v2) -> {
                // Don't put an "and" the first time
                if (!filterExpression.toString().isEmpty()) {
                    filterExpression.append(" and ");
                }
                //date is a reserved expression by dynamodb uses #d as expressionattributename
                if (v1.equals(":date")) {
                    filterExpression.append("#d").append(" = ").append(v1);
                    expression.put("#d", "date");
                } else {
                    filterExpression.append(v1.substring(1)).append(" = ").append(v1);
                }
            });

            values.put(":v1", new AttributeValue().withS(scooterId));

            DynamoDBQueryExpression<Booking> queryExp =
                    new DynamoDBQueryExpression<>();
            queryExp.withKeyConditionExpression("scooterId = :v1")
                    .withExpressionAttributeValues(values)
                    .withFilterExpression(filterExpression.toString())
                    .withConsistentRead(true);
            if (!expression.isEmpty()) {
                queryExp.setExpressionAttributeNames(expression);
            }
            return mapper.query(Booking.class, queryExp);
        }
    }

    public List<Booking> bookingsByDate(LocalDate bookingDate) throws IOException {
        return bookingsByDate(bookingDate, null);
    }

    /**
     *
     * @param bookingDate Used as hashkey for query
     * @param filter used for filtering results. If the filter contains the scooterId field the method will query with the scooterId as range key instead of of filtering with it.
     * @return results of matching bookings
     * @throws IOException from dynamodb.
     */
    public List<Booking> bookingsByDate(LocalDate bookingDate, Map<String, String> filter) throws IOException {

        Booking booking = new Booking();
        booking.setStartDate(bookingDate);

        if(!Optional.ofNullable(filter).isPresent()) {
            DynamoDBQueryExpression<Booking> queryExpression =
                    new DynamoDBQueryExpression<>();
            queryExpression.setHashKeyValues(booking);
            queryExpression.setIndexName("startTimeIndex");
            queryExpression.setConsistentRead(false);

            return mapper.query(Booking.class, queryExpression);
        }
        else {

            Map<String, AttributeValue> values = new HashMap<>();
            filter.forEach((s1, s2) -> values.put(":" + s1, new AttributeValue().withS(s2)));
            StringBuilder filterExpression = new StringBuilder();

            values.forEach((v1, v2) -> {
                if (!filterExpression.toString().isEmpty()) {
                    filterExpression.append(" and ");
                }
                filterExpression.append(v1.substring(1)).append(" = ").append(v1);
            });

            logger.info(filterExpression.toString());
            DynamoDBQueryExpression<Booking> queryExpression =
                    new DynamoDBQueryExpression<>();

            queryExpression.withHashKeyValues(booking);
            queryExpression
                    .setExpressionAttributeValues(values);
            if (!filterExpression.toString().isEmpty()) {
                queryExpression.setFilterExpression(filterExpression.toString());
            }
            queryExpression.withIndexName("startTimeIndex")
                    .withConsistentRead(false);


            return mapper.query(Booking.class, queryExpression);
        }
    }



    public Booking save(Booking booking) throws IOException {

            logger.info("Booking - save(): " + booking.toString());
            this.mapper.save(booking);
            return booking;
    }

    public void update(Booking booking) throws  IOException {

        logger.info("User - update(): " + booking.toString());
        //TODO: Optimistic Locking och Condition Expressions???

        DynamoDBMapperConfig dynamoDBMapperConfig = new DynamoDBMapperConfig.Builder()
                .withConsistentReads(DynamoDBMapperConfig.ConsistentReads.CONSISTENT)
                .withSaveBehavior(DynamoDBMapperConfig.SaveBehavior.UPDATE_SKIP_NULL_ATTRIBUTES)
                .build();
        this.mapper.save(booking, dynamoDBMapperConfig);

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return
                Objects.equals(scooterId, booking.scooterId) &&
                Objects.equals(bookingId, booking.bookingId) &&
                Objects.equals(userId, booking.userId) &&
                Objects.equals(startTime, booking.startTime) &&
                Objects.equals(endTime, booking.endTime) &&
                Objects.equals(startDate, booking.startDate) &&
                Objects.equals(endDate, booking.endDate) &&
                bookingStatus == booking.bookingStatus &&
                Objects.equals(trips, booking.trips);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, scooterId, bookingId, userId, startTime, endTime, startDate, endDate, bookingStatus, trips, client, mapper, dynamoDB, logger, sb);
    }
}
//TODO: if booking is not checked out in allotted time, will we want to keep it in the db, delete it or move it to another db? it should cancel to leave timespan available for others to book
