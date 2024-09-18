$(document).ready(function(){

    $('#records-table').DataTable({
        "ajax":{
            "url": "/api/records",
            "dataSrc": ""
        },
        "columns":[
            {"data": "name"},
            {"data": "department"},
            {"data": "city"}
        ]
    }

    )
}


)