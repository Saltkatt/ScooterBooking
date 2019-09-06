package com.wirelessiths;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.wirelessiths.dal.DynamoDBAdapter;
import com.wirelessiths.service.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@DynamoDBTable(tableName = "SETTINGS_TABLE_NAME")
public class AdminConstraints {

    private static final String SETTINGS_TABLE_NAME = System.getenv("SETTINGS_TABLE_NAME");

    private String field;
    private int value;
    // seconds or minutes
    private int buffer;
    private int maxDuration;
    //check if admin is true?
    public AuthService admin;

    private static DynamoDBAdapter db_adapter;
    private final AmazonDynamoDB client;
    private final DynamoDBMapper mapper;
    private final DynamoDB dynamoDB;

    private final Logger logger = LogManager.getLogger(this.getClass());
    private static StringBuilder sb = new StringBuilder();

    public AdminConstraints() {
        // build the mapper config
        DynamoDBMapperConfig mapperConfig = DynamoDBMapperConfig.builder()
                .withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(SETTINGS_TABLE_NAME))
                .build();
        // get the db adapter
        this.db_adapter = DynamoDBAdapter.getInstance();
        this.client = this.db_adapter.getDbClient();
        this.dynamoDB = this.db_adapter.getDynamoDB();
        // create the mapper with config
        this.mapper = this.db_adapter.createDbMapper(mapperConfig);
    }


    @DynamoDBRangeKey(attributeName = "buffer")
    public int getBuffer() {
        return buffer;
    }

    public void setBuffer(int buffer) {
        this.buffer = buffer;
    }

    @DynamoDBRangeKey(attributeName = "maxDuration")
    public int getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(int maxDuration) {
        this.maxDuration = maxDuration;
    }

    @Override
    public String toString() {
        return "Settings{" +
                ", buffer='" + buffer + '\'' +
                ", maxDuration='" + maxDuration + '\'' +
                ", admin='" + admin + '\'' +
                '}';
    }

/*

  public void bufferTime(Booking booking) {
      boolean admin = AuthService.isAdmin();
      int buffer = sc.nextInt();

      if (admin == true) {
          // 5 minutes = 300 seconds
          System.out.println("Enter buffer in seconds: ");

          System.out.println("New buffer is: " + buffer);

      }
  }*/
}