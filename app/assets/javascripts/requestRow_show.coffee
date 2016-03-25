row_id = location.pathname.split('/')[2]
$ ->
  $.get "/requestRows/" + row_id, (rows) ->
    $.each rows, (index, row) ->
      $("#requestId").html row.requestId
      $("#productId").html row.productId
      $("#productorId").html row.productorId
      $("#quantity").html row.quantity
      $("#statusstatus").html row.status
