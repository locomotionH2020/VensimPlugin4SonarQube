module.exports = [
    {
        path: '/qaGetCategories',
        method: 'GET',
        template:
            [
                {name: "labour", level: 1, super_category: "null"},
                {name: "RES_employment", level: 2, super_category: "labour"},

            ]
    }
]
