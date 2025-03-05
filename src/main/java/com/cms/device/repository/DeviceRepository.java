package com.cms.device.repository;

import com.cms.device.entity.Device;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface DeviceRepository extends MongoRepository<Device, String> {
    @Aggregation(pipeline = {
            "{ $group: { _id: '$vendorId', deviceCount: { $sum: 1 } } }", // Nhóm theo vendorId và đếm thiết bị
            })
    List<Map<String, Object>> getDeviceStatsByVendor();


    @Aggregation({
            "{ '$match': { 'vendorId': { '$in': ?0 } } }", // Lọc theo danh sách vendorId
            "{ '$group': { '_id': '$type', 'count': { '$sum': 1 } } }"
    })
    List<Map<String, Object>> getDeviceTypeDistributionByVendors(List<String> vendorIds);

    @Aggregation({
            "{ '$match': { 'createDate': { '$gte': ?0 } } }", // Lọc theo ngày (7 ngày gần nhất)
            "{ '$group': { '_id': { '$dateToString': { 'format': '%Y-%m-%d', 'date': '$createDate' } }, 'count': { '$sum': 1 } } }",
            "{ '$sort': { '_id': 1 } }" // Sắp xếp theo ngày tăng dần
    })
    List<Map<String, Object>> getNewDevicesDays(Date fromDate);

    @Aggregation(pipeline = {
            "{ '$group': { '_id': { 'vendorId': '$vendorId', 'type': '$type' }, 'count': { '$sum': 1 } } }",
            "{ '$lookup': { 'from': 'Vendor', 'localField': '_id.vendorId', 'foreignField': '_id', 'as': 'vendorInfo' } }",
            "{ '$unwind': { 'path': '$vendorInfo', 'preserveNullAndEmptyArrays': true } }",
            "{ '$project': { "
                    + "'vendorId': '$_id.vendorId', "
                    + "'deviceType': '$_id.type', "
                    + "'deviceCount': '$count', "
                    + "'vendorName': '$vendorInfo.name' "
                    + "} }",
            "{ '$sort': { 'vendorId': 1 } }"
    })
    List<Map<String, Object>> getVendorDeviceStats(Pageable pageable);

    @Aggregation(pipeline = {
            "{ '$group': { '_id': { 'vendorId': '$vendorId', 'type': '$type' } } }",
            "{ '$count': 'total' }"
    })
    Long getTotalVendorDeviceStats();

}
