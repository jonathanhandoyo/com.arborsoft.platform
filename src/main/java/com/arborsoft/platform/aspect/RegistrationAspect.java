package com.arborsoft.platform.aspect;

import com.arborsoft.platform.domain.BaseNode;
import com.arborsoft.platform.exception.DatabaseOperationException;
import com.arborsoft.platform.service.Neo4jService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Aspect
public class RegistrationAspect {
    private static final Logger LOG = LoggerFactory.getLogger(RegistrationAspect.class);

    @Autowired
    private Neo4jService neo4j;

    @Before("execution(* com.arborsoft.platform.service.Neo4jService.save(com.arborsoft.platform.domain.BaseNode))")
    public void registerNode(JoinPoint joinPoint) throws Throwable {
        BaseNode node = (BaseNode) joinPoint.getArgs()[0];
        for (String text: node.getLabels()) {
            Label label = DynamicLabel.label(text);
            try {
                this.neo4j.register(label, node.keySet());
            } catch (DatabaseOperationException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    @Before("execution(* com.arborsoft.platform.service.Neo4jService.relate(..))")
    public void registerRelationship(JoinPoint joinPoint) throws Throwable {
        //TODO
    }
}
