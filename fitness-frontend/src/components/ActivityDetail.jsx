import React, { useEffect, useState } from 'react'
import { useParams } from 'react-router'
import { getActivityDetail } from '../services/api';
import {
  Box,
  Card,
  CardContent,
  Divider,
  Typography
} from '@mui/material';

const ActivityDetail = () => {

  const { id } = useParams();

  const [activity, setActivity] = useState(null);

  useEffect(() => {

    const fetchActivityDetail = async () => {

      try {

        const response = await getActivityDetail(id);

        console.log(response.data);

        setActivity(response.data);

      } catch (error) {
        console.error(error);
      }
    }

    fetchActivityDetail();

  }, [id]);

  if (!activity) {
    return <Typography>Loading...</Typography>
  }

  return (

    <Box sx={{ maxWidth: 800, mx: 'auto', p: 2 }}>

      {/* Activity Details */}

      <Card sx={{ mb: 2 }}>

        <CardContent>

          <Typography variant="h5" gutterBottom>
            Activity Details
          </Typography>

          <Typography>
            Type: {activity.activityType}
          </Typography>

          <Typography>
            Duration: {activity.duration || 'N/A'} minutes
          </Typography>

          <Typography>
            Calories Burned: {activity.caloriesBurned || 'N/A'}
          </Typography>

          <Typography>
            Date: {new Date(activity.createdAt).toLocaleString()}
          </Typography>

        </CardContent>

      </Card>

      {/* AI Recommendation */}

      <Card>

        <CardContent>

          <Typography variant="h5" fontWeight="bold" gutterBottom>
            AI Recommendation
          </Typography>

          {/* Analysis */}

          <Typography
            variant="h6"
            fontWeight="bold"
            gutterBottom
          >
            Analysis
          </Typography>

          <Typography paragraph>
            {activity.recommendations}
          </Typography>

          <Divider sx={{ my: 2 }} />

          {/* Improvements */}

          <Typography
            variant="h6"
            fontWeight="bold"
            gutterBottom
          >
            Improvements
          </Typography>

          {activity.improvement?.map((item, index) => (

            <Typography key={index} paragraph>
              • {item}
            </Typography>

          ))}

          <Divider sx={{ my: 2 }} />

          {/* Suggestions */}

          <Typography
            variant="h6"
            fontWeight="bold"
            gutterBottom
          >
            Suggestions
          </Typography>

          {activity.suggestions?.map((item, index) => (

            <Typography key={index} paragraph>
              • {item}
            </Typography>

          ))}

          <Divider sx={{ my: 2 }} />

          {/* Safety */}

          <Typography
            variant="h6"
            fontWeight="bold"
            gutterBottom
          >
            Safety Guidelines
          </Typography>

          {activity.safety?.map((item, index) => (

            <Typography key={index} paragraph>
              • {item}
            </Typography>

          ))}

        </CardContent>

      </Card>

    </Box>
  )
}

export default ActivityDetail