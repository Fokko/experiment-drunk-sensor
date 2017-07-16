# The Drunk sensor

This project generates data and pushes it onto kafka (Topic `datastream`). The goal is to take windows of this data, apply simple linear regression and push it back to kafka under a different topic (`response`). The first one who has the right answer, gets points assigned. One caveat, the sensor had a wild party this weekend and is still a bit tipsy. Therefore there is a bit of noise on the signal, and also the messages might arrive late, sometimes really late...

## How to get it started

Run a local instance of [Wurstmeister Kafka/Zookeeper](https://hub.docker.com/r/wurstmeister/kafka/). Please make sure that you set the following in the docker-compose yml:
```
KAFKA_ADVERTISED_HOST_NAME: 192.168.192.175
KAFKA_CREATE_TOPICS: "datastream:1:1,response:1:1"
```
This IP needs to be your own. The topics need to be pre-allocated otherwise something goes wrong with the assignment of the topic leader. Now you can start the project:

```
sbt run
```

This will push results to your local kafka, from which you can easily read using your preferred stream technology. If you want to submit results, you can easily write them back:

```
{"eventTime": 1500144885,"submittedBy": "Fokko","intercept": 2481.2343323490245,"slope":34.25661969635175}
```

This `eventTime` is the beginning of the 30seconds time window. `submittedBy` needs to be your own name, and the intercept and the slope are the weight of your regression. The order of submissions is decided by kafka, first in, first to get the points.  