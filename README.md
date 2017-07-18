# sqs 文档 https://aws.amazon.com/cn/documentation/sqs/
# key 登录认证 http://docs.aws.amazon.com/zh_cn/sdk-for-java/v1/developer-guide/java-dg-logging.html
# API https://docs.aws.amazon.com/zh_cn/AWSSimpleQueueService/latest/APIReference/API_CreateQueue.html
# sqs 注意事项
 - 标准队列
 1、标准队列会尽量保持消息顺序，但可能有一条消息的多个副本可能不按顺序传送。如果您的系统要求保留该顺序，我们建议使用 FIFO (先进先出)队列 或在每条消息中添加排序信息，以便您在收到消息后对其重新排序
 2、Amazon SQS 会在多台服务器上存储消息的副本，以实现冗余和高可用性。在极少数情况下，当您接收或删除消息时，存储消息副本的某台服务器可能不可用。
    如果出现这种情况，则该不可用服务器上的消息副本将不会被删除，并且您在接收消息时可能会再次获得该消息副本。您应将应用程序设计为幂等 应用程序 (多次处理同一消息时，它们不应受到不利影响)。
   