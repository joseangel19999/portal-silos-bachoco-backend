package com.bachuco.persistence.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;
import com.bachuco.model.EmpleadoInternoRequest;
import com.bachuco.model.EmpleadoInternoResponse;

@Component
public class EmpleadoInternoJdbcRepository {

	private final JdbcTemplate jdbcTemplate;
	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private SimpleJdbcCall simpleJdbcCall;
	private final DataSource dataSource;
	
	public EmpleadoInternoJdbcRepository(JdbcTemplate jdbcTemplate,
			NamedParameterJdbcTemplate namedParameterJdbcTemplate, DataSource dataSource) {
		this.jdbcTemplate = jdbcTemplate;
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
		this.dataSource = dataSource;
	}
	
	public List<EmpleadoInternoResponse> findAll(){
		String sql = """
				 SELECT
				    e.EMPLEADO_ID,
					e.NOMBRE,
			        e.CORREO,
                    u.USUARIO,
			        e.TIPO_EMPLEADO,
				    ei.TC_DEPARTAMENTO_ID AS AREA_ID,
				    ei.TC_PUESTO_ID AS ROL_ID
			    FROM tc_empleado e
			    JOIN tc_empleado_interno ei on e.EMPLEADO_ID=ei.TC_EMPLEADO_ID
			    JOIN tc_usuario u on e.TC_USUARIO_ID=u.USUARIO_ID
			     where e.TIPO_EMPLEADO=1
			    and u.USUARIO_TIPO=1
			    and e.ESTATUS = '1'
			    GROUP BY 
			   e.EMPLEADO_ID,
					e.NOMBRE,
			        e.CORREO,
                    u.USUARIO,
			        e.TIPO_EMPLEADO,
				    ei.TC_DEPARTAMENTO_ID,
				    ei.TC_PUESTO_ID
				    """;
		return jdbcTemplate.query(sql, new RowMapper<EmpleadoInternoResponse>() {
            @Override
            public EmpleadoInternoResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
            	EmpleadoInternoResponse p = new EmpleadoInternoResponse();
                p.setId(rs.getInt("EMPLEADO_ID"));
                p.setNombre(rs.getString("NOMBRE"));
                p.setCorreo(rs.getString("CORREO"));
                p.setUsuario(rs.getString("USUARIO"));
                p.setTipoEmpleado(rs.getInt("TIPO_EMPLEADO"));
                p.setDepartamentoId(rs.getInt("AREA_ID"));
                p.setPerfilId(rs.getInt("ROL_ID"));
                p.setPuestoId(rs.getInt("ROL_ID"));
                return p;
            }
        });
	}
	
	public void save(EmpleadoInternoRequest request) throws Exception {
		 simpleJdbcCall = new SimpleJdbcCall(dataSource)
	                .withProcedureName("sp_insert_emp_interno");
		 Integer idGenerado=-1;
			 Map<String, Object> result = simpleJdbcCall.execute(
		                Map.of(
		                        "p_nombre", request.getNombre(),
		                        "p_correo", request.getCorreo(),
		                        "p_usuario", request.getUsuario(),
		                        "p_dept_id", request.getDepartamentoId(),
		                        "p_puesto_id", request.getPuestoId()
		                )
		        );
			 idGenerado = (Integer) result.get("p_emp_interno_id");
	}
	
	public void update(Integer id,EmpleadoInternoRequest request) {
		 simpleJdbcCall = new SimpleJdbcCall(dataSource)
	                .withProcedureName("sp_update_empleado_interno");
		 try {
			 Map<String, Object> result = simpleJdbcCall.execute(
		                Map.of(
		                		"p_empleado_id", id,
		                        "p_nombre", request.getNombre(),
		                        "p_correo", request.getCorreo(),
		                        "p_usuario", request.getUsuario(),
		                        "p_depto_id", request.getDepartamentoId(),
		                        "p_puesto_id", request.getPuestoId()
		                )
		        );
		 }catch (Exception e) {
			e.printStackTrace();
		}
	}
	private EmpleadoInternoResponse toDomain(EmpleadoInternoRequest request,Integer id) {
		EmpleadoInternoResponse response= new EmpleadoInternoResponse();
		response.setId(id);
		response.setCorreo(request.getCorreo());
		response.setNombre(request.getNombre());
		response.setUsuario(request.getUsuario());
		response.setDepartamentoId(request.getDepartamentoId());
		response.setPerfilId(request.getPuestoId());
		return response;
	}
	
	public void delete(Integer id) {
		 String sql = "UPDATE tc_empleado SET ESTATUS = '0',EMP_FECHA_BAJA=sysdate() WHERE EMPLEADO_ID =?";
	     jdbcTemplate.update(sql,id);
	}
	
}
