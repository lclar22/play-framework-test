row_id = location.pathname.split('/')[2]

$ ->
  $.get "/storekeepers/" + row_id, (rows) ->
    $.each rows, (index, row) ->
      $("#name").html row.nombre
      $("#carnet").html row.carnet
      $("#telefono").html row.telefono
      $("#direccion").html row.direccion

