row_id = location.pathname.split('/')[2]
$ ->
  $.get "/insumos/" + row_id, (rows) ->
    $.each rows, (index, row) ->
      $("#nombre").html row.nombre
      $("#costo").html row.costo
      $("#porcentage").html row.porcentage
      $("#descripcion").html row.descripcion
      $("#unidad").html row.unidad
