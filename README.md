# web-traffic-analysis-xc

# 四个模块的说明

# tracker：主要用于采集用户访问网站行为的数据
# tracker-logServer：用于接收tracker发送过来的日志信息并存储在本地文件中
# backend：用于数据的清洗和解析处理，最终入库到hive
# frontend：用于数据的分析和可视化