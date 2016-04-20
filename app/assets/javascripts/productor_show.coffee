row_id = location.pathname.split('/')[2]

$ ->
  $.get "/productores/" + row_id, (rows) ->
    $.each rows, (index, row) ->
      $("#name").html row.nombre
      $("#carnet").html row.carnet
      $("#telefono").html row.telefono
      $("#direccion").html row.direccion
      $("#cuenta").html row.cuenta
      $("#asociacion").html row.asociacion
      $("#totalDebt").html row.totalDebt
      $("#numberPayment").html row.numberPayment
      $("#position").html row.position
