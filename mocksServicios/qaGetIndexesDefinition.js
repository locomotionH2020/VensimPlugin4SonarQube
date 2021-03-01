module.exports = [
    {
        path: '/integrationTest/qaGetIndexesDefinition',
        method: 'POST',
        template:
            [{
                "definition": "",
                "indexName": "SUBSCRIPT_NOT_IN_DB_I",
                "values": ["VALUE_2", "VALUE_1"]
            }, {
                "definition": "",
                "indexName": "SUBSCRIPT_NOT_IN_DB_II",
                "values": ["VALUE_1", "VALUE_2"]
            }]
    },
    {
        path: '/qaGetIndexesDefinition',
        method: 'POST',
        template: [{
            "definition": "",
            "indexName": "SUBSCRIPT_NOT_IN_DB_I",
            "values": ["VALUE_2", "VALUE_1"]
        }, {
            "definition": "",
            "indexName": "SUBSCRIPT_NOT_IN_DB_II",
            "values": ["VALUE_1", "VALUE_2"]
        }]
    }
]
