row_id = location.pathname.split('/')[2]
$ ->
  $.get "/productRequests/" + row_id, (rows) ->
    $.each rows, (index, row) ->
      $("#date").html row.date
      $("#veterinario").html row.veterinario
      $("#storekeeper").html row.storekeeper
      $("#status").html row.status
