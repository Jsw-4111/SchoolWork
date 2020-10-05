using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class CatAI : MonoBehaviour
{
    public Transform player;
    public float angle = 0;
    public float radius = 0;
    public float moveSpeed;
    public Transform patrolPath;
    public Transform tracker;
    private Transform[] patrolPoints;
    public int pathTolerance;
    public int pathIndex = 0;
    public float maxVel;

    public void Start()
    {
        int i = 0;
        patrolPoints = new Transform[patrolPath.childCount];
        foreach(Transform child in patrolPath.transform)
        {
            patrolPoints[i] = child;
            i++;
        }
    }

    public void OnDrawGizmos()
    {
        Gizmos.color = Color.yellow;
        Vector3 FOVLeft = Quaternion.AngleAxis(angle, transform.up) * transform.forward * radius;
        Vector3 FOVRight = Quaternion.AngleAxis(-angle, transform.up) * transform.forward * radius;
        
        Gizmos.DrawRay(transform.position, FOVLeft);
        Gizmos.DrawRay(transform.position, FOVRight);
        
        Vector3 FOVUp = Quaternion.AngleAxis(-angle, transform.right) * transform.forward * radius;
        Vector3 FOVDown = Quaternion.AngleAxis(angle, transform.right) * transform.forward * radius;
        
        Gizmos.DrawRay(transform.position, FOVUp);
        Gizmos.DrawRay(transform.position, FOVDown);
        
        Gizmos.color = Color.blue;
        Gizmos.DrawWireSphere(transform.position, radius);
        if (spotted())
        {
            Gizmos.color = Color.red;
        }
        else
        {
            Gizmos.color = Color.blue;
        }
        Gizmos.DrawRay(transform.position, (player.position - transform.position).normalized * radius);
    }
    public bool spotted()
    {
        RaycastHit hit;
        float angleBetween = Vector3.Angle(transform.forward, (player.position - transform.position).normalized);
        if(angleBetween <= angle)
        {
            Ray ray = new Ray(transform.position, player.position - transform.position);
            if(Physics.Raycast(ray, out hit, radius))
            {
                if(hit.transform == player)
                {
                    return true;
                }
            }
        }
        return false;
    }

   public void FixedUpdate()
    {
        Rigidbody body = GetComponent<Rigidbody>();
        move(body);
    }

    public void move(Rigidbody body)
    {
        if(spotted())
        {
            tracker.position = new Vector3(player.position.x, 
                transform.position.y, player.position.z);
            transform.LookAt(tracker);
            if(body.velocity.magnitude <= maxVel)
                body.AddForce(transform.forward.normalized*moveSpeed);
            // transform.position = transform.position + transform.forward.normalized*moveSpeed;
        }
        else
        {
            pathIndex = pathIndex%(patrolPath.childCount-1); // Loops the paths
            Vector3 distanceToPoint = new Vector3(patrolPoints[pathIndex].position.x 
                - transform.position.x, 0, patrolPoints[pathIndex].position.z 
                - transform.position.z);
            if(distanceToPoint.magnitude < pathTolerance)
            {
                pathIndex++;
            }
            tracker.position = new Vector3(patrolPoints[pathIndex].position.x, 
                transform.position.y, patrolPoints[pathIndex].position.z);
            transform.LookAt(tracker);
            if(body.velocity.magnitude <= maxVel)
                body.AddForce(transform.forward.normalized*moveSpeed);
            // if() // If we reach the point
            //    pathIndex++;
        }
    }

    public void OnCollisionEnter(Collision collision)
    {
        if(collision.gameObject == player.gameObject)
        {
            Debug.Log("Game end!");
            // Load other scene here, load UI elements or something
            // Application.quit
        }
    }
}